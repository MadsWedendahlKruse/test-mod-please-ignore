package mwk.testmod.common.block.cable.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.ibm.icu.impl.Pair;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.cable.CableBlock;
import mwk.testmod.common.util.energy.EnergyStorageVariableCapacity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableNetwork {

    private final ServerLevel level;
    private final IEnergyStorage energyStorage;
    // TODO: Rest of this https://docs.neoforged.net/docs/datastorage/capabilities/
    private final Map<BlockPos, Map<Direction, BlockCapabilityCache<IEnergyStorage, Direction>>> connections;
    // private final Set<BlockCapabilityCache<IEnergyStorage, Direction>> connections;
    private int transferRate;

    /**
     * Creates a new cable network for the given level.
     * 
     * @param level The level to create the network in.
     */
    public CableNetwork(ServerLevel level) {
        // TEMP
        this.transferRate = 1024;
        this.level = level;
        this.energyStorage = new EnergyStorageVariableCapacity(this.transferRate) {
            @Override
            public int getMaxEnergyStored() {
                return connections.size() * transferRate;
            };
        };
        this.connections = new HashMap<>();
    }

    public ServerLevel getLevel() {
        return level;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    /**
     * Adds a block to the network.
     * 
     * @param pos The position of the block.
     * @param face The face of the block which is connected to the cable.
     */
    public void addBlock(BlockPos pos, Direction face) {
        TestMod.LOGGER.debug("Adding face " + face + " of block "
                + level.getBlockState(pos).getBlock() + " at " + pos + " in network " + this);
        IEnergyStorage energyStorage =
                level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, face);
        // If the energy storage is null for the given face, that might just be because the block
        // has temporarily disabled the capability, but it might reappear later, so we need to check
        // if there are any other sides that have the capability
        if (energyStorage != null) {
            TestMod.LOGGER.debug(level.getBlockState(pos).getBlock() + ", canExtract: "
                    + energyStorage.canExtract() + ", canReceive: " + energyStorage.canReceive());
            connections.putIfAbsent(pos, new HashMap<>());
            connections.get(pos).put(face,
                    BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, level, pos, face,
                            () -> true, () -> onCapInvalidated(level, pos, face)));
            // connections.add(BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, level,
            // pos, face));
        }
    }

    /**
     * Removes a block from the network.
     * 
     * @param pos The position of the block.
     * @param direction The face of the block which is connected to the cable.
     */
    public void removeBlock(BlockPos pos, Direction direction) {
        // TODO: If we stop using set I don't know if we can just remove from both
        TestMod.LOGGER.debug("Removing face " + direction + " of block "
                + level.getBlockState(pos).getBlock() + " at " + pos + " in network " + this);
        var cacheMap = connections.get(pos);
        if (cacheMap != null) {
            cacheMap.remove(direction);
            if (cacheMap.isEmpty()) {
                connections.remove(pos);
            }
        }
    }

    /**
     * Merges this network with another network.
     * 
     * @param other The other network to merge with.
     */
    public void merge(CableNetwork other) {
        for (var entry : other.connections.entrySet()) {
            BlockPos pos = entry.getKey();
            for (Direction face : entry.getValue().keySet()) {
                this.connections.putIfAbsent(pos, new HashMap<>());
                this.connections.get(pos).put(face, entry.getValue().get(face));
            }
        }
    }

    /**
     * Distributes energy to the connected blocks.
     */
    public void distributeEnergy() {
        // If there are no connections, there's nothing to do
        if (connections.isEmpty()) {
            return;
        }
        for (var connection : connections.entrySet()) {
            for (var cacheMap : connection.getValue().entrySet()) {
                IEnergyStorage connectionEnergyStorage = cacheMap.getValue().getCapability();
                if (connectionEnergyStorage == null) {
                    continue;
                }
                // We don't want to give more energy than we have
                int energyToReceive = Math.min(transferRate, energyStorage.getEnergyStored());
                int energyReceived = connectionEnergyStorage.receiveEnergy(energyToReceive, false);
                energyStorage.extractEnergy(energyReceived, false);
            }
        }
    }

    private void onCapInvalidated(ServerLevel level, BlockPos pos, Direction face) {
        Block capabilityBlock = level.getBlockState(pos).getBlock();
        TestMod.LOGGER.debug("Invalidating capability for face " + face + " of block "
                + capabilityBlock + " at " + pos + " in network " + this);
        // We need to update the CABLE which is opposite of the face that was invalidated
        BlockPos cablePos = pos.relative(face);
        BlockState cableState = level.getBlockState(cablePos);
        if (cableState.getBlock() instanceof CableBlock cableBlock) {
            BlockState newCableState = cableBlock.calculateState(level, cablePos, cableState);
            level.setBlockAndUpdate(cablePos, newCableState);
        }
    }
}
