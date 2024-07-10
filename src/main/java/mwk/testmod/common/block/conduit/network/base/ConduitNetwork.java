package mwk.testmod.common.block.conduit.network.base;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.ConduitType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.INBTSerializable;

/**
 * Base class for all conduit networks. This class is responsible for managing the network data and
 * serializing it to disk.
 * 
 * @param <T> The type of payload that the network will be transferring, e.g. ItemStack for item
 *        networks, FluidStack for fluid networks, Integer for energy networks.
 */
public abstract class ConduitNetwork<T> implements INBTSerializable<CompoundTag> {

    public static final String NBT_TAG_SIZE = "size";
    public static final String NBT_TAG_POS = "pos";

    // This is primarily used for accessing the correct data structure in the
    // network manager
    protected final ConduitType type;
    // Graph representation of the network. The key is the position of the conduit
    protected final Map<BlockPos, ArrayList<BlockPos>> graph;
    // The master position is the position of the conduit that is in charge of
    // serializing the network data and saving it to disk
    protected BlockPos masterPos;

    protected ConduitNetwork(ConduitType type) {
        this.type = type;
        this.graph = new HashMap<>();
    }

    protected BlockPos[] getAllNeighbors(BlockPos pos) {
        BlockPos[] neighbors = new BlockPos[6];
        for (Direction direction : Direction.values()) {
            neighbors[direction.ordinal()] = pos.relative(direction);
        }
        return neighbors;
    }

    public void add(BlockPos pos) {
        graph.put(pos, new ArrayList<>());
        for (BlockPos neighbor : getAllNeighbors(pos)) {
            if (graph.containsKey(neighbor)) {
                graph.get(pos).add(neighbor);
                graph.get(neighbor).add(pos);
            }
        }
    }

    public void remove(BlockPos pos) {
        ArrayList<BlockPos> neighbors = graph.remove(pos);
        for (BlockPos neighbor : neighbors) {
            graph.get(neighbor).remove(pos);
        }
    }

    public int getSize() {
        return graph.size();
    }

    public Set<BlockPos> getPositions() {
        return graph.keySet();
    }

    // public void merge(ConduitNetwork other) {
    // for (BlockPos pos : other.getPositions().keySet()) {
    // add(pos);
    // }
    // }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
        TestMod.LOGGER.info("Conduit at " + masterPos + " is now the master of " + this);
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public boolean isMasterPos(BlockPos conduit) {
        return masterPos.equals(conduit);
    }

    public void clearMasterPos() {
        // TODO: Unncecessary to remove the masterPos from the graph?
        // When this is called from the manager I think it's already removed
        graph.remove(masterPos);
        masterPos = null;
    }

    public ConduitType getType() {
        return type;
    }

    /**
     * Creates an empty payload. This is used to initialize the aggregate of the payloads that have
     * been received by the receivers in the network so far.
     * 
     * @param payload The payload to copy the empty payload from (not necessarily used).
     * @return An empty payload.
     */
    @NotNull
    protected abstract T createEmptyPayload(T payload);

    /**
     * Checks if the payload is empty. This is used to determine if the network should continue
     * searching for a recipient for the payload. E.g. if the network is an energy network, the
     * payload would be the amount of energy to transfer. If the payload is 0, then the network
     * should stop searching for a recipient.
     * 
     * @param payload The payload to check.
     * @return True if the payload is empty, false otherwise.
     */
    protected abstract boolean isPayloadEmpty(T payload);

    /**
     * Transfers the payload to the receiver. This is where the actual transfer of the payload
     * should happen. This method should also handle the case where the payload is too large for the
     * receiver to handle. This method has to check if the receiver is an instance of the expected
     * capability, e.g. IEnergyStorage for energy networks (TODO: Not very nice).
     * 
     * @param receiver The receiver of the payload.
     * @param payload The payload to transfer.
     * @param simulate If the transfer should be simulated.
     * @return The payload that has been received by the receiver.
     */
    protected abstract T transferPayload(@NotNull Object receiver, T payload, boolean simulate);

    /**
     * Aggregates the payloads that have been received by the receivers in the network. This method
     * is responsible for combining the payloads into a single payload that can be returned to the
     * sender to indicate the total amount of the payload that has been received by the receivers.
     * 
     * @param aggregate The aggregate of the payloads that have been received so far.
     * @param receivedPayload The payload that has been received by the receiver.
     * @return The new aggregate of the payloads.
     */
    protected abstract T aggregatePayloads(T aggregate, T receivedPayload);

    /**
     * Gets the remaining payload after a payload has successfully been transferred to the receivers
     * in the network.
     * 
     * @param payload The payload that was sent.
     * @param receivedPayload The payload that has been received by the receivers in the network.
     * @return The remaining payload that has not yet been transferred to the receivers in the
     *         network.
     */
    protected abstract T getRemainingPayload(T payload, T receivedPayload);

    /**
     * This method is responsible for receiving the payload and transferring it to the receivers in
     * the network.
     * 
     * @param level The level that the network is in.
     * @param start The position of the conduit that is sending the payload.
     * @param payload The payload to transfer.
     * @param simulate If the transfer should be simulated.
     * @return The payload that has been received by the receivers in the network.
     */
    public T receivePayload(ServerLevel level, BlockPos start, T payload, boolean simulate) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        T payloadAggregate = createEmptyPayload(payload);

        while (!queue.isEmpty() && !isPayloadEmpty(payload)) {
            BlockPos current = queue.poll();
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);
                if (!visited.contains(neighbor) && graph.containsKey(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
                // If the neighbor is a conduit, we don't want to transfer the payload to it
                // TODO: Maybe not the nice way to check this?
                if (ConduitNetworkManager.getInstance().getNetwork(neighbor) != null) {
                    continue;
                }
                var receiver = level.getCapability(type.getCapability(), neighbor,
                        direction.getOpposite());
                if (receiver == null) {
                    continue;
                }
                T receivedPayload = transferPayload(receiver, payload, simulate);
                payload = getRemainingPayload(payload, receivedPayload);
                payloadAggregate = aggregatePayloads(payloadAggregate, receivedPayload);
            }
        }
        return payloadAggregate;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_TAG_SIZE, graph.size());
        BlockPos[] posArray = graph.keySet().toArray(new BlockPos[0]);
        for (int i = 0; i < graph.size(); i++) {
            tag.putLong(NBT_TAG_POS + i, posArray[i].asLong());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains(NBT_TAG_SIZE)) {
            int size = tag.getInt(NBT_TAG_SIZE);
            graph.clear();
            for (int i = 0; i < size; i++) {
                if (tag.contains(NBT_TAG_POS + i)) {
                    add(BlockPos.of(tag.getLong(NBT_TAG_POS + i)));
                }
            }
        }
    }

}
