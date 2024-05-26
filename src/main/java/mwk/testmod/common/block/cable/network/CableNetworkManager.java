package mwk.testmod.common.block.cable.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.ibm.icu.impl.Pair;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.cable.CableBlock;
import mwk.testmod.common.block.cable.ConnectorType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Singleton class that manages all cable networks in the world.
 */
public class CableNetworkManager {

    public static final CableNetworkManager INSTANCE = new CableNetworkManager();

    private final Map<BlockPos, CableNetwork> networkMap;
    private final Map<CableNetwork, Set<BlockPos>> reverseNetworkMap;

    private CableNetworkManager() {
        // TODO: Not sure what the best collection type is here
        this.networkMap = new HashMap<>();
        this.reverseNetworkMap = new HashMap<>();
    }

    public static CableNetworkManager getInstance() {
        return INSTANCE;
    }

    public void tick() {
        for (CableNetwork network : reverseNetworkMap.keySet()) {
            network.distributeEnergy();
        }
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
                return reverseNetworkMap.get(n1).size() - reverseNetworkMap.get(n2).size();
            }).get();
            for (CableNetwork network : neighborNetworks) {
                if (network != largestNetwork) {
                    mergeNetworks(largestNetwork, network);
                }
            }
            addCableToNetwork(pos, largestNetwork);
        }
        // The network the cable is connected to
        CableNetwork network = networkMap.get(pos);
        // Connect neighboring block entities to the network
        List<Pair<BlockPos, BlockState>> neighbors = getNeighborBlocks(level, pos, state);
        for (Pair<BlockPos, BlockState> neighbor : neighbors) {
            // If it's not a cable, it must be a block(?)
            if (!(neighbor.second.getBlock() instanceof CableBlock)) {
                BlockPos neighborPos = neighbor.first;
                // Get the face of the neighbor that is connected to the cable
                Direction face = Direction.fromDelta(pos.getX() - neighborPos.getX(),
                        pos.getY() - neighborPos.getY(), pos.getZ() - neighborPos.getZ());
                network.addBlock(neighbor.first, face);
            }
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
            removeCableFromNetwork(pos);
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
     * Updates the networks in the given level after one of the neighboring blocks of a cable has
     * been placed or removed. This handles adding and removing blocks(machines) from the networks.
     * 
     * @param pos The position of the cable whose neighbors have changed.
     * @param state The state of the cable whose neighbors have changed.
     * @param neighborState The state of the neighbor that has changed.
     * @param direction The direction of the neighbor that has changed.
     */
    public void updateNetworks(BlockPos pos, BlockState state, BlockState neighborState,
            Direction direction) {
        // If the neighbor is a cable, we don't need to do anything
        if (neighborState.getBlock() instanceof CableBlock) {
            return;
        }
        CableNetwork network = networkMap.get(pos);
        if (network == null) {
            return;
        }
        BlockPos neighborPos = pos.relative(direction);
        Direction face = direction.getOpposite();
        // Check if the cable expects a connection in the given direction
        if (state.getValue(
                CableBlock.CONNECTOR_PROPERTIES[direction.ordinal()]) == ConnectorType.BLOCK) {
            // If the neighbor is air we need to remove the block from the network
            if (neighborState.isAir()) {
                network.removeBlock(neighborPos, face);
            }
        } else {
            // If the cable doesn't expect a connection in the given direction, and the neighbor is
            // a block, we need to add the block to the network
            network.addBlock(neighborPos, face);
        }
    }

    /**
     * Gets the energy storage of the network the cable at the given position is connected to.
     * 
     * @param pos The position of the cable to get the energy storage of.
     * @return The energy storage of the network the cable is connected to, or null if the cable is
     *         not connected to a network.
     */
    public IEnergyStorage getEnergyStorage(BlockPos pos) {
        CableNetwork network = networkMap.get(pos);
        return network != null ? network.getEnergyStorage() : null;
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
        CableNetwork network = new CableNetwork(level);
        TestMod.LOGGER.debug("Creating new network " + network + " at " + pos);
        addCableToNetwork(pos, network);
    }

    /**
     * Creates a new network with the given positions in the given level.
     * 
     * @param level The level to create the network in.
     * @param positions The positions of the blocks to create the network with.
     */
    private void createNetwork(ServerLevel level, Set<BlockPos> positions) {
        CableNetwork network = new CableNetwork(level);
        TestMod.LOGGER.debug("Creating new network " + network + " at " + positions);
        for (BlockPos pos : positions) {
            addCableToNetwork(pos, network);
        }
    }

    /**
     * Adds the block at the given position to the given network.
     * 
     * @param pos The position of the block to add.
     * @param network The network to add the block to.
     */
    private void addCableToNetwork(BlockPos pos, CableNetwork network) {
        // Rather be safe than sorry
        if (network != null) {
            TestMod.LOGGER.debug("Adding cable at " + pos + " to network " + network);
            networkMap.put(pos, network);
            reverseNetworkMap.computeIfAbsent(network, k -> new HashSet<>()).add(pos);
        }
    }

    /**
     * Removes the block at the given position from its network.
     * 
     * @param pos The position of the block to remove.
     */
    private void removeCableFromNetwork(BlockPos pos) {
        CableNetwork network = networkMap.remove(pos);
        if (network != null) {
            TestMod.LOGGER.debug("Removing cable at " + pos + " from network " + network);
            Set<BlockPos> positions = reverseNetworkMap.get(network);
            if (positions != null) {
                positions.remove(pos);
                if (positions.isEmpty()) {
                    TestMod.LOGGER.debug("Network " + network + " is now empty, removing");
                    reverseNetworkMap.remove(network);
                }
            }
        }
    }

    /**
     * Merges two networks into one.
     *
     * @param network1 The network to merge into.
     * @param network2 The network to merge.
     */
    private void mergeNetworks(CableNetwork network1, CableNetwork network2) {
        if (network1 == network2) {
            return;
        }
        TestMod.LOGGER.debug("Merging network " + network1 + " with " + network2);
        network1.merge(network2);
        Set<BlockPos> positionsToUpdate = reverseNetworkMap.get(network2);
        if (positionsToUpdate != null) {
            // Copy the set to avoid concurrent modification exceptions
            List<BlockPos> positions = new ArrayList<>(positionsToUpdate);
            for (BlockPos pos : positions) {
                // Remove the block from its current network (old network)
                removeCableFromNetwork(pos);
                // Add the block to the new network
                addCableToNetwork(pos, network1);
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
        Set<BlockPos> positions = reverseNetworkMap.get(originalNetwork);
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
                        createNetwork(level, newNetworkPositions);
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
}
