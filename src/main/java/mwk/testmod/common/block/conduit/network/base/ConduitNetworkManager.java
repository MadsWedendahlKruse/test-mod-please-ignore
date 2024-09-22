package mwk.testmod.common.block.conduit.network.base;

import com.ibm.icu.impl.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.ConduitBlock;
import mwk.testmod.common.block.conduit.ConduitBlockEntity;
import mwk.testmod.common.block.conduit.ConduitConnectionType;
import mwk.testmod.common.block.conduit.ConduitType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Singleton class that manages all conduit networks in the world.
 */
public class ConduitNetworkManager {

    public static ConduitNetworkManager instance;

    private final Map<BlockPos, ConduitNetwork<?, ?>> networkMap;

    private ConduitNetworkManager() {
        // TODO: Not sure what the best collection type is here
        this.networkMap = new HashMap<>();
    }

    public static ConduitNetworkManager getInstance() {
        if (instance == null) {
            instance = new ConduitNetworkManager();
        }
        return instance;
    }

    /**
     * Connects the conduit at the given position to a network in the given level. This handles
     * creating a new network if necessary, as well as merging networks if the conduit is placed
     * between two existing networks.
     *
     * @param level The level to connect the conduit in.
     * @param pos   The position of the conduit to connect.
     * @param state The state of the conduit to connect.
     */
    public void connectToNetwork(ServerLevel level, BlockPos pos, BlockState state) {
        // Depending on the number of neighboring networks, we either create a new
        // network, connect to an existing network, or merge multiple networks
        if (!(state.getBlock() instanceof ConduitBlock conduitBlock)) {
            return;
        }
        ConduitType type = conduitBlock.getType();
        Set<ConduitNetwork<?, ?>> neighborNetworks = getNeighborNetworks(pos, type);
        if (neighborNetworks.isEmpty()) {
            // Create a new network
            createNetwork(level, pos, type);
        } else {
            // Connect to an existing network, merge if necessary
            // TODO: Not sure if this is the best way to find the largest network
            ConduitNetwork<?, ?> largestNetwork = neighborNetworks.stream().max(
                    Comparator.comparingInt(ConduitNetwork::getSize)).get();
            for (ConduitNetwork<?, ?> network : neighborNetworks) {
                if (network != largestNetwork) {
                    mergeNetworks(level, largestNetwork, network);
                }
            }
            addConduitToNetwork(level, pos, largestNetwork);
        }
    }

    /**
     * Disconnects the conduit at the given position from its network. This handles removing the
     * block from the network, as well as potentially splitting the network into multiple networks.
     *
     * @param level The level to disconnect the conduit in.
     * @param pos   The position of the conduit to disconnect.
     * @param state The state of the conduit to disconnect.
     */
    public void disconnectFromNetwork(ServerLevel level, BlockPos pos, BlockState state) {
        // Remove the block from its current network. If the block was alone in its
        // network, the network will be removed as well. If the block was part of a larger network,
        // the network might be split into multiple networks.
        ConduitNetwork<?, ?> originalNetwork = getNetwork(pos);
        if (originalNetwork != null) {
            removeConduitFromNetwork(level, pos);
            // If it was connected to more than one conduit, we might need to split the
            // network
            List<Pair<BlockPos, BlockState>> neighbors = getNeighborBlocks(level, pos, state);
            int conduitCount = neighbors.stream().filter(neighbor -> {
                return neighbor.second.getBlock() instanceof ConduitBlock;
            }).toArray().length;
            if (conduitCount > 1) {
                splitNetwork(level, originalNetwork);
            }
        }
    }

    /**
     * Gets the conduit network at the given position.
     *
     * @param pos The position of the conduit to get the network of.
     * @return The conduit network at the given position, or null if there is no network.
     */
    public ConduitNetwork<?, ?> getNetwork(BlockPos pos) {
        return networkMap.get(pos);
    }

    /**
     * Gets the neighbors of the conduit at the given position, excluding the conduits in the same
     * network.
     *
     * @param pos  The position of the conduit to get the neighbors of.
     * @param type The type of neighbors to get.
     * @return A set of all neighboring networks.
     */
    private Set<ConduitNetwork<?, ?>> getNeighborNetworks(BlockPos pos, ConduitType type) {
        Set<ConduitNetwork<?, ?>> neighbors = new HashSet<>();
        ConduitNetwork<?, ?> network = networkMap.get(pos);
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            ConduitNetwork<?, ?> neighbor = networkMap.get(neighborPos);
            if (neighbor != null && neighbor != network && neighbor.getType() == type) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * Creates a new network at the given position in the given level.
     *
     * @param level The level to create the network in.
     * @param pos   The position of the conduit to create the network at.
     * @param type  The type of the conduit to create the network for.
     */
    private void createNetwork(ServerLevel level, BlockPos pos, ConduitType type) {
        ConduitNetwork<?, ?> network = type.createNetwork();
        TestMod.LOGGER.debug("Creating new network " + network + " at " + pos);
        network.setMasterPos(pos);
        addConduitToNetwork(level, pos, network);
    }

    /**
     * Adds the conduit at the given position to the given network.
     *
     * @param level   The level the conduit is in.
     * @param pos     The position of the conduit to add.
     * @param network The network to add the conduit to.
     */
    private void addConduitToNetwork(ServerLevel level, BlockPos pos,
            ConduitNetwork<?, ?> network) {
        // Rather be safe than sorry
        if (network == null) {
            return;
        }
        TestMod.LOGGER.debug("Adding conduit at " + pos + " to network " + network);
        networkMap.put(pos, network);
        network.add(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ConduitBlockEntity<?> conduitBlockEntity) {
            conduitBlockEntity.setNetwork(network);
        }
    }

    /**
     * Removes the conduit at the given position from its network.
     *
     * @param level The level the conduit is in.
     * @param pos   The position of the conduit to remove.
     */
    private void removeConduitFromNetwork(ServerLevel level, BlockPos pos) {
        // ConduitNetwork network = networkMap.remove(pos);
        ConduitNetwork<?, ?> network = networkMap.remove(pos);
        if (network == null) {
            return;
        }
        TestMod.LOGGER.debug("Removing conduit at " + pos + " from network " + network);
        network.remove(pos);
        // Find a new master if the current one is removed
        if (network.isMasterPos(pos)) {
            network.clearMasterPos();
            if (network.getSize() > 0) {
                network.setMasterPos(network.getPositions().iterator().next());
            }
        }
    }

    /**
     * Merges two networks into one.
     *
     * @param level    The level the networks are in.
     * @param network1 The network to merge into.
     * @param network2 The network to merge.
     */
    private void mergeNetworks(ServerLevel level, ConduitNetwork<?, ?> network1,
            ConduitNetwork<?, ?> network2) {
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
                removeConduitFromNetwork(level, pos);
                // Add the block to the new network
                addConduitToNetwork(level, pos, network1);
            }
        }
    }

    /**
     * Splits a network into multiple networks if necessary.
     *
     * @param level           The level the network is in.
     * @param originalNetwork The network to split.
     */
    private void splitNetwork(ServerLevel level, ConduitNetwork<?, ?> originalNetwork) {
        TestMod.LOGGER.debug("Splitting network " + originalNetwork);

        Set<BlockPos> positions = originalNetwork.getPositions();
        if (positions == null || positions.isEmpty()) {
            return;
        }

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
                            if (positions.contains(neighborPos) && !visited.contains(neighborPos)) {
                                toVisit.add(neighborPos);
                            }
                        }
                    }
                }

                if (!newNetworkPositions.isEmpty()) {
                    Iterator<BlockPos> it = newNetworkPositions.iterator();
                    BlockPos firstPos = it.next();
                    createNetwork(level, firstPos, originalNetwork.getType());
                    ConduitNetwork<?, ?> newNetwork = networkMap.get(firstPos);
                    while (it.hasNext()) {
                        addConduitToNetwork(level, it.next(), newNetwork);
                    }
                }
            }
        }
    }

    /**
     * Gets the neighboring blocks of the block at the given position.
     *
     * @param level The level to get the neighbors in.
     * @param pos   The position of the block to get the neighbors of.
     * @param state The state of the block to get the neighbors of.
     * @return A list of pairs of positions and states of the neighboring blocks.
     */
    private List<Pair<BlockPos, BlockState>> getNeighborBlocks(ServerLevel level, BlockPos pos,
            BlockState state) {
        List<Pair<BlockPos, BlockState>> neighbors = new ArrayList<>();
        Direction[] directions = Direction.values();
        for (int i = 0; i < ConduitBlock.CONNECTOR_PROPERTIES.length; i++) {
            if (state.getValue(ConduitBlock.CONNECTOR_PROPERTIES[i])
                    != ConduitConnectionType.NONE) {
                BlockPos neighborPos = pos.relative(directions[i]);
                neighbors.add(Pair.of(neighborPos, level.getBlockState(neighborPos)));
            }
        }
        return neighbors;
    }

    public void serializeNetworkNBT(BlockPos pos, CompoundTag tag) {
        ConduitNetwork<?, ?> network = networkMap.get(pos);
        if (network != null) {
            tag.put("network", network.serializeNBT());
        }
    }

    public void deserializeNetworkNBT(BlockPos pos, CompoundTag tag, ConduitType type) {
        ConduitNetwork<?, ?> network = type.createNetwork();
        network.deserializeNBT(tag.getCompound("network"));
        for (BlockPos position : network.getPositions()) {
            networkMap.put(position, network);
        }
        network.setMasterPos(pos);
    }
}
