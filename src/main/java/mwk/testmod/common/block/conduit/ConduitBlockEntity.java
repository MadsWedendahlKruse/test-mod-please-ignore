package mwk.testmod.common.block.conduit;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import mwk.testmod.common.block.conduit.network.base.ConduitNetworkManager;
import mwk.testmod.common.block.interfaces.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

public abstract class ConduitBlockEntity<T> extends BlockEntity implements ITickable {

    public static final String NBT_TAG_MASTER = "isMaster";
    public static final String NBT_TAG_CONDUIT_TYPE = "conduitType";

    // Cache for the capabilities of the neighboring blocks
    private final BlockCapabilityCache<T, Direction>[] connections;
    private boolean capsInvalidated;
    // The network this conduit is part of
    protected ConduitNetwork<?, ?> network;
    protected ConduitType conduitType;
    // One conduit is in charge of serializing the network data
    private boolean isMaster;
    // Whether or not this conduit has connections to blocks other than conduits
    private boolean hasConnections;
    // If caps are invalidated the conduit might gain or lose connections
    private boolean hasConnectionsValid;

    public ConduitBlockEntity(BlockEntityType<?> type, ConduitType conduitType, BlockPos pos,
            BlockState blockState) {
        super(type, pos, blockState);
        this.conduitType = conduitType;
        this.connections = new BlockCapabilityCache[Direction.values().length];
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ConduitNetwork<?, ?> network =
                ConduitNetworkManager.getInstance().getNetwork(worldPosition);
        if (network != null) {
            setNetwork(network);
            BlockPos masterPos = network.getMasterPos();
            if (masterPos != null && masterPos.equals(worldPosition)) {
                isMaster = true;
            }
        }
        if (level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                BlockPos neigborPos = worldPosition.relative(direction);
                Direction neighborFace = direction.getOpposite();
                connections[direction.ordinal()] = BlockCapabilityCache.create(
                        conduitType.getCapability(), serverLevel, neigborPos, neighborFace,
                        () -> !this.isRemoved(), () -> onCapInvalidated());
            }
        }
    }

    @Override
    public void tick() {
        // This is probably not going to happen, but just in case
        // TODO: Remove this check if it's not needed
        // I've seen this once, so it's better to keep it for now
        if (network == null) {
            if (level.getServer().getTickCount() % 20 == 0)
                TestMod.LOGGER.error("ConduitBlockEntity at {} has no network", worldPosition);
            return;
        }
        for (BlockCapabilityCache<T, Direction> connection : connections) {
            // We need to query the capability to trigger the invalidation callback
            T cap = connection.getCapability();
        }
        if (capsInvalidated) {
            capsInvalidated = false;
            updateBlockState();
        }
    }

    private void onCapInvalidated() {
        capsInvalidated = true;
    }

    private void updateBlockState() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof ConduitBlock conduitBlock) {
            MinecraftServer server = level.getServer();
            // An exception is thrown if calculateState is called while the world is saving
            if (server != null && !server.isCurrentlySaving()) {
                BlockState newState = conduitBlock.calculateState(level, worldPosition, state);
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

    public void setNetwork(ConduitNetwork<?, ?> network) {
        this.network = network;
        this.conduitType = network.getType();
    }

    public boolean hasConnections() {
        if (!hasConnectionsValid) {
            hasConnections = false;
            for (BlockCapabilityCache<?, Direction> connection : connections) {
                BlockEntity blockEntity = this.level.getBlockEntity(connection.pos());
                if (connection.getCapability() != null
                        && !(blockEntity instanceof ConduitBlockEntity)) {
                    hasConnections = true;
                    break;
                }
            }
            hasConnectionsValid = true;
        }
        return hasConnections;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean(NBT_TAG_MASTER, isMaster);
        if (isMaster) {
            ConduitNetworkManager.getInstance().serializeNetworkNBT(worldPosition, tag);
        }
        tag.putInt(NBT_TAG_CONDUIT_TYPE, conduitType.ordinal());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NBT_TAG_CONDUIT_TYPE)) {
            conduitType = ConduitType.values()[tag.getInt(NBT_TAG_CONDUIT_TYPE)];
        }
        if (tag.contains(NBT_TAG_MASTER)) {
            isMaster = tag.getBoolean(NBT_TAG_MASTER);
            if (isMaster && conduitType != null) {
                ConduitNetworkManager.getInstance().deserializeNetworkNBT(worldPosition, tag,
                        conduitType);
            }
        }
    }
}
