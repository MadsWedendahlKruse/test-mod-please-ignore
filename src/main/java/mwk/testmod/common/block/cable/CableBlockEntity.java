package mwk.testmod.common.block.cable;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.cable.network.CableNetwork;
import mwk.testmod.common.block.cable.network.CableNetworkManager;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableBlockEntity extends BlockEntity implements ITickable {

    public static final String NBT_TAG_MASTER = "isMaster";

    // Cache for the energy storage capabilities of the neighboring blocks
    private final BlockCapabilityCache<IEnergyStorage, Direction>[] connections;
    // Cache for the energy storage capability of this block
    private IEnergyStorage energyStorage;
    // One cable is in charge of serializing the network data
    private boolean isMaster;

    public CableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.CABLE_ENTITY_TYPE.get(), pos, blockState);
        this.connections = new BlockCapabilityCache[Direction.values().length];
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                BlockPos neigborPos = worldPosition.relative(direction);
                Direction neighborFace = direction.getOpposite();
                connections[direction.ordinal()] = BlockCapabilityCache.create(
                        Capabilities.EnergyStorage.BLOCK, serverLevel, neigborPos, neighborFace,
                        () -> !this.isRemoved(), () -> onCapInvalidated());
            }
        }
        CableNetwork network = CableNetworkManager.getInstance().getNetwork(worldPosition);
        if (network != null) {
            BlockPos masterPos = network.getMasterPos();
            if (masterPos != null && masterPos.equals(worldPosition)) {
                isMaster = true;
            }
        }
        updateEnergyStorage();
    }

    @Override
    public void tick() {
        // This is probably not going to happen, but just in case
        // TODO: Remove this check if it's not needed
        if (energyStorage == null) {
            if (level.getServer().getTickCount() % 20 == 0)
                TestMod.LOGGER.error("CableBlockEntity at {} has no energy storage capability",
                        worldPosition);
            return;
        }
        for (BlockCapabilityCache<IEnergyStorage, Direction> connection : connections) {
            IEnergyStorage connectionEnergyStorage = connection.getCapability();
            // We don't want the cable to feed energy back into itself
            if (connectionEnergyStorage == null || connectionEnergyStorage == energyStorage) {
                continue;
            }
            int energyToReceive = Math.min(1024, energyStorage.getEnergyStored());
            int energyReceived = connectionEnergyStorage.receiveEnergy(energyToReceive, false);
            energyStorage.extractEnergy(energyReceived, false);
            if (energyReceived > 0) {
                setChanged();
            }
        }
    }

    public IEnergyStorage getEnergyHandler(Direction direction) {
        CableNetwork network = CableNetworkManager.getInstance().getNetwork(worldPosition);
        return network != null ? network.getEnergyStorage() : null;
    }

    public void updateEnergyStorage() {
        energyStorage = getEnergyHandler(null);
    }

    private void onCapInvalidated() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof CableBlock cableBlock) {
            MinecraftServer server = level.getServer();
            // An exception is thrown if calculateState is called while the world is saving
            if (server != null && !server.isCurrentlySaving()) {
                BlockState newState = cableBlock.calculateState(level, worldPosition, state);
                this.level.setBlockAndUpdate(this.worldPosition, newState);
            }
        }
    }

    public boolean setMaster(boolean isMaster) {
        if (this.isMaster == isMaster) {
            return false;
        }
        this.isMaster = isMaster;
        setChanged();
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean(NBT_TAG_MASTER, isMaster);
        if (isMaster) {
            CableNetworkManager.getInstance().serializeNetworkNBT(worldPosition, tag);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NBT_TAG_MASTER)) {
            isMaster = tag.getBoolean(NBT_TAG_MASTER);
            if (isMaster) {
                CableNetworkManager.getInstance().deserializeNetworkNBT(worldPosition, tag);
            }
        }
    }
}
