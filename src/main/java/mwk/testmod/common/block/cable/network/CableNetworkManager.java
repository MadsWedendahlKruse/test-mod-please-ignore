package mwk.testmod.common.block.cable.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import com.ibm.icu.impl.Pair;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.cable.CableBlock;
import mwk.testmod.common.block.cable.CableBlockEntity;
import mwk.testmod.common.block.cable.ConnectorType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Singleton class that manages all cable networks in the world.
 */
public class CableNetworkManager {

    public static CableNetworkManager instance;

    private final Map<BlockPos, CableNetwork> networkMap;

    private CableNetworkManager() {
        // TODO: Not sure what the best collection type is here
        this.networkMap = new HashMap<>();
    }

    public CableNetworkManager create() {
        return new CableNetworkManager();
    }

    public static CableNetworkManager getInstance() {
        if (instance == null) {
            instance = new CableNetworkManager();
        }
        return instance;
    }

    /**
     * Connects the cable at the given position to a network in the given level. This handles
     * creating a new network if necessary, as well as merging networks if the cable is placed
     * between two existing networks.
     * 
     * @param level The level to connect the cable in.
     * @param pos The position of the cable to connect.
     * @param state The state of the cable to connect.
     */
    public void connectToNetwork(ServerLevel level, BlockPos pos, BlockState state) {
        // Depending on the number of neighboring networks, we either create a new network, connect
        // to an existing network, or merge multiple networks
        Set<CableNetwork> neighborNetworks = getNeighborNetworks(pos);
        if (neighborNetworks.isEmpty()) {
            // Create a new network
            createNetwork(level, pos);
        } else {
            // Connect to an existing network, merge if necessary
            // TODO: Not sure if this is the best way to find the largest network
            CableNetwork largestNetwork = neighborNetworks.stream().max((n1, n2) -> {
                return Integer.compare(n1.getSize(), n2.getSize());
            }).get();
            for (CableNetwork network : neighborNetworks) {
                if (network != largestNetwork) {
                    mergeNetworks(level, largestNetwork, network);
                }
            }
            addCableToNetwork(level, pos, largestNetwork);
        }
    }

    /**
     * Disconnects the cable at the given position from its network. This handles removing the block
     * from the network, as well as potentially splitting the network into multiple networks.
     * 
     * @param level The level to disconnect the cable in.
     * @param pos The position of the cable to disconnect.
     * @param state The state of the cable to disconnect.
     */
    public void disconnectFromNetwork(ServerLevel level, BlockPos pos, BlockState state) {
        // Remove the block from its current network. If the block was alone in its network, the
        // network will be removed as well. If the block was part of a larger network, the network
        // might be split into multiple networks.
        CableNetwork originalNetwork = networkMap.get(pos);
        if (originalNetwork != null) {
            removeCableFromNetwork(level, pos);
            // If it was connected to more than one cable, we might need to split the network
            List<Pair<BlockPos, BlockState>> neighbors = getNeighborBlocks(level, pos, state);
            int cableCount = neighbors.stream().filter(neighbor -> {
                return neighbor.second.getBlock() instanceof CableBlock;
            }).toArray().length;
            if (cableCount > 1) {
                splitNetwork(level, originalNetwork);
            }
        }
    }

    /**
     * Gets the cable network at the given position.
     * 
     * @param pos The position of the cable to get the network of.
     * @return The cable network at the given position, or null if there is no network.
     */
    public CableNetwork getNetwork(BlockPos pos) {
        return networkMap.get(pos);
    }

    /**
     * Gets the neighbors of the cable at the given position, excluding the cables in the same
     * network.
     * 
     * @param pos The position of the cable to get the neighbors of.
     * @return A set of all neighboring networks.
     */
    private Set<CableNetwork> getNeighborNetworks(BlockPos pos) {
        Set<CableNetwork> neighbors = new HashSet<>();
        CableNetwork network = networkMap.get(pos);
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            CableNetwork neighbor = networkMap.get(neighborPos);
            if (neighbor != null && neighbor != network) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * Creates a new network at the given position in the given level.
     * 
     * @param level The level to create the network in.
     * @param pos The position of the cable to create the network at.
     */
    private void createNetwork(ServerLevel level, BlockPos pos) {
        // TODO: Don't hardcode the transfer rate
        CableNetwork network = new CableNetwork(1024);
        TestMod.LOGGER.debug("Creating new network " + network + " at " + pos);
        network.setMasterPos(pos);
        addCableToNetwork(level, pos, network);
    }

    /**
     * Adds the cable at the given position to the given network.
     * 
     * @param level The level the cable is in.
     * @param pos The position of the cable to add.
     * @param network The network to add the cable to.
     */
    private void addCableToNetwork(ServerLevel level, BlockPos pos, CableNetwork network) {
        // Rather be safe than sorry
        if (network != null) {
            TestMod.LOGGER.debug("Adding cable at " + pos + " to network " + network);
            networkMap.put(pos, network);
            network.add(pos);
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CableBlockEntity cableBlockEntity) {
            cableBlockEntity.updateEnergyStorage();
            blockEntity.invalidateCapabilities();
        }
    }

    /**
     * Removes the cable at the given position from its network.
     * 
     * @param level The level the cable is in.
     * @param pos The position of the cable to remove.
     */
    private void removeCableFromNetwork(ServerLevel level, BlockPos pos) {
        CableNetwork network = networkMap.remove(pos);
        if (network != null) {
            TestMod.LOGGER.debug("Removing cable at " + pos + " from network " + network);
            network.remove(pos);
            // Find a new master if the current one is removed
            if (network.isMasterPos(pos)) {
                network.clearMasterPos();
                if (network.getSize() > 0) {
                    network.setMasterPos(network.getPositions().iterator().next());
                }
            }
        }
    }

    /**
     * Merges two networks into one.
     *
     * @param level The level the networks are in.
     * @param network1 The network to merge into.
     * @param network2 The network to merge.
     */
    private void mergeNetworks(ServerLevel level, CableNetwork network1, CableNetwork network2) {
        if (network1 == network2) {
            return;
        }
        TestMod.LOGGER.debug("Merging network " + network1 + " with " + network2);
        Set<BlockPos> positionsToUpdate = network2.getPositions();
        if (positionsToUpdate != null) {
            // Copy the set to avoid concurrent modification exceptions
            List<BlockPos> positions = new ArrayList<>(positionsToUpdate);
            for (BlockPos pos : positions) {
                // Remove the block from its current network (old network)
                removeCableFromNetwork(level, pos);
                // Add the block to the new network
                addCableToNetwork(level, pos, network1);
            }
        }
    }

    /**
     * Splits a network into multiple networks if necessary.
     *
     * @param level The level the network is in.
     * @param originalNetwork The network to split.
     */
    private void splitNetwork(ServerLevel level, CableNetwork originalNetwork) {
        TestMod.LOGGER.debug("Splitting network " + originalNetwork);
        Set<BlockPos> positions = originalNetwork.getPositions();
        int storedEnergy = originalNetwork.getEnergyStorage().getEnergyStored();
        if (positions != null && !positions.isEmpty()) {
            Set<BlockPos> visited = new HashSet<>();
            for (BlockPos pos : positions) {
                if (!visited.contains(pos)) {
                    Set<BlockPos> newNetworkPositions = new HashSet<>();
                    Queue<BlockPos> toVisit = new LinkedList<>();
                    toVisit.add(pos);

                    while (!toVisit.isEmpty()) {
                        BlockPos currentPos = toVisit.poll();
                        if (visited.add(currentPos)) {
                            newNetworkPositions.add(currentPos);
                            for (Direction direction : Direction.values()) {
                                BlockPos neighborPos = currentPos.relative(direction);
                                if (positions.contains(neighborPos)
                                        && !visited.contains(neighborPos)) {
                                    toVisit.add(neighborPos);
                                }
                            }
                        }
                    }

                    if (!newNetworkPositions.isEmpty()) {
                        Iterator<BlockPos> it = newNetworkPositions.iterator();
                        BlockPos firstPos = it.next();
                        createNetwork(level, firstPos);
                        CableNetwork newNetwork = networkMap.get(firstPos);
                        while (it.hasNext()) {
                            addCableToNetwork(level, it.next(), newNetwork);
                        }
                        int received =
                                newNetwork.getEnergyStorage().receiveEnergy(storedEnergy, false);
                        storedEnergy -= received;
                    }
                }
            }
        }
    }

    /**
     * Gets the neighboring blocks of the block at the given position.
     * 
     * @param level The level to get the neighbors in.
     * @param pos The position of the block to get the neighbors of.
     * @param state The state of the block to get the neighbors of.
     * @return A list of pairs of positions and states of the neighboring blocks.
     */
    private List<Pair<BlockPos, BlockState>> getNeighborBlocks(ServerLevel level, BlockPos pos,
            BlockState state) {
        List<Pair<BlockPos, BlockState>> neighbors = new ArrayList<>();
        Direction[] directions = Direction.values();
        for (int i = 0; i < CableBlock.CONNECTOR_PROPERTIES.length; i++) {
            if (state.getValue(CableBlock.CONNECTOR_PROPERTIES[i]) != ConnectorType.NONE) {
                BlockPos neighborPos = pos.relative(directions[i]);
                neighbors.add(Pair.of(neighborPos, level.getBlockState(neighborPos)));
            }
        }
        return neighbors;
    }

    public void serializeNetworkNBT(BlockPos pos, CompoundTag tag) {
        CableNetwork network = networkMap.get(pos);
        if (network != null) {
            tag.put("network", network.serializeNBT());
        }
    }

    public void deserializeNetworkNBT(BlockPos pos, CompoundTag tag) {
        CableNetwork network = new CableNetwork(0);
        network.deserializeNBT(tag.getCompound("network"));
        for (BlockPos position : network.getPositions()) {
            networkMap.put(position, network);
        }
        network.setMasterPos(pos);
    }
}
