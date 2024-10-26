package mwk.testmod.common.block.conduit;

import java.util.HashMap;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import mwk.testmod.common.block.conduit.network.base.ConduitNetworkManager;
import mwk.testmod.common.block.conduit.network.capabilites.NetworkCapabilityProvider;
import mwk.testmod.common.block.interfaces.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

/**
 * Base class for all conduit block entities.
 *
 * @param <C> The type of capability that the conduit will be using, e.g. IEnergyStorage for energy
 *            conduits, IFluidHandler for fluid conduits, IItemHandler for item conduits.
 */
public abstract class ConduitBlockEntity<C> extends BlockEntity implements ITickable {

    // Cache for the capabilities of the neighboring blocks
    private final BlockCapabilityCache<C, Direction>[] connections;
    private boolean capsInvalidated;
    // The network this conduit is part of
    protected ConduitNetwork<C, ?> network;
    protected ConduitType conduitType;
    // Whether this conduit has connections to blocks other than conduits
    private boolean hasConnections;
    // If caps are invalidated the conduit might gain or lose connections
    private boolean hasConnectionsValid;
    // Capabilities exposed by this conduit
    protected final HashMap<Direction, C> capabilities;

    public ConduitBlockEntity(BlockEntityType<?> type, ConduitType conduitType, BlockPos pos,
            BlockState blockState) {
        super(type, pos, blockState);
        this.conduitType = conduitType;
        this.connections = new BlockCapabilityCache[Direction.values().length];
        this.capabilities = new HashMap<>();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            ConduitNetworkManager.getInstance()
                    .connectToNetwork(serverLevel, worldPosition, getBlockState());
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
        // This happens once in a while, think it might happen if the world is loaded
        // after the game crashed
        if (network == null) {
            TestMod.LOGGER.error("ConduitBlockEntity at {} has no network", worldPosition);
            // We can just make this error fix itself by connecting to the network
            if (level instanceof ServerLevel serverLevel) {
                ConduitNetworkManager.getInstance()
                        .connectToNetwork(serverLevel, worldPosition, getBlockState());
            }
//            return;
        }
        BlockState state = this.getBlockState();
        for (BlockCapabilityCache<C, Direction> connection : connections) {
            // We need to query the capability to trigger the invalidation callback
            C cap = connection.getCapability();
            // Skip neighbors that are conduits
            if (cap instanceof NetworkCapabilityProvider<?>) {
                continue;
            }
            // The direction is the face of the block that is connected to this conduit,
            // so the opposite direction is the direction of the block from the conduit
            // perspective (which is what we're interested in)
            Direction direction = connection.context().getOpposite();
            ConduitConnectionType conduitConnectionType = state.getValue(
                    ConduitBlock.CONNECTOR_PROPERTIES[direction.ordinal()]);
            if (conduitConnectionType == ConduitConnectionType.PULL && cap != null) {
                if (level instanceof ServerLevel serverLevel) {
                    network.pullPayload(serverLevel, worldPosition, direction, cap);
                }
            }
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

    public void setNetwork(ConduitNetwork<?, ?> network) {
        this.network = (ConduitNetwork<C, ?>) network;
//        this.conduitType = network.getType();
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

    public C getCapability(Direction direction) {
        return capabilities.computeIfAbsent(direction, this::createNewCapability);
    }

    protected abstract C createNewCapability(Direction direction);
}
