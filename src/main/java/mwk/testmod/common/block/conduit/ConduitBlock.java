package mwk.testmod.common.block.conduit;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import mwk.testmod.common.block.conduit.network.base.ConduitNetworkManager;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.interfaces.IWrenchable;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;

/**
 * Almost all of this is taken from https://www.mcjty.eu/docs/1.20.4_neo/ep5#introduction
 */
public class ConduitBlock extends Block
        implements EntityBlock, IWrenchable, SimpleWaterloggedBlock {

    // Same order as Direction.values()
    public static final EnumProperty<ConduitConnectionType> DOWN =
            EnumProperty.create("down", ConduitConnectionType.class);
    public static final EnumProperty<ConduitConnectionType> UP =
            EnumProperty.create("up", ConduitConnectionType.class);
    public static final EnumProperty<ConduitConnectionType> NORTH =
            EnumProperty.create("north", ConduitConnectionType.class);
    public static final EnumProperty<ConduitConnectionType> SOUTH =
            EnumProperty.create("south", ConduitConnectionType.class);
    public static final EnumProperty<ConduitConnectionType> WEST =
            EnumProperty.create("west", ConduitConnectionType.class);
    public static final EnumProperty<ConduitConnectionType> EAST =
            EnumProperty.create("east", ConduitConnectionType.class);
    public static final EnumProperty<ConduitConnectionType>[] CONNECTOR_PROPERTIES =
            new EnumProperty[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};

    private final ConduitType type;

    public ConduitBlock(Properties properties, ConduitType type) {
        super(properties.noOcclusion());
        makeShapes();
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
        this.type = type;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return type.getBlockEntity(pos, state);
    }

    public ConduitType getType() {
        return type;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        // Lambda expression that implements the BlockEntityTicker interface.
        return (lvl, pos, st, be) -> {
            if (be instanceof ITickable tickable) {
                tickable.tick();
            }
        };
    }

    /**
     * @return The connector type of the block at the given position.
     */
    private ConduitConnectionType getConnectionType(BlockGetter world, BlockPos connectorPos,
            Direction facing) {
        BlockPos neighborPos = connectorPos.relative(facing);
        if (world.getBlockState(neighborPos).getBlock() instanceof ConduitBlock other
                && other.getType() == type) {
            return ConduitConnectionType.CONDUIT;
        } else if (isConnectable(world, connectorPos, facing)) {
            // Check if the conduit is already connected to the block
            BlockState state = world.getBlockState(connectorPos);
            if (state.getBlock() instanceof ConduitBlock) {
                ConduitConnectionType conduitConnectionType = state.getValue(
                        CONNECTOR_PROPERTIES[facing.ordinal()]);
                if (conduitConnectionType.hasConnector()) {
                    return conduitConnectionType;
                }
            }
            return ConduitConnectionType.BIDIRECTIONAL;
        } else {
            return ConduitConnectionType.NONE;
        }
    }

    /**
     * Check if the block at the given position is connectable to the conduit.
     */
    public boolean isConnectable(BlockGetter world, BlockPos connectorPos, Direction facing) {
        BlockPos pos = connectorPos.relative(facing);
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) {
            return false;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }
        Direction opposite = facing.getOpposite();
        Level level = blockEntity.getLevel();
        // TODO: Capability cache?
        return level.getCapability(type.getCapability(), pos, opposite) != null;
    }

    @Nonnull
    public BlockState calculateState(LevelAccessor world, BlockPos pos, BlockState state) {
        ArrayList<ConduitConnectionType> connectors = new ArrayList<>(Direction.values().length);
        for (Direction facing : Direction.values()) {
            connectors.add(getConnectionType(world, pos, facing));
        }
        BlockState newState = state;
        for (int i = 0; i < CONNECTOR_PROPERTIES.length; i++) {
            newState = newState.setValue(CONNECTOR_PROPERTIES[i], connectors.get(i));
        }
        return newState;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = calculateState(level, pos, defaultBlockState()).setValue(
                BlockStateProperties.WATERLOGGED,
                level.getFluidState(pos).getType() == Fluids.WATER);
        if (level instanceof ServerLevel serverLevel) {
            ConduitNetworkManager.getInstance().connectToNetwork(serverLevel, pos, state);
        }
        return state;
    }

    @Nonnull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState,
            boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (level instanceof ServerLevel serverLevel && newState.getBlock() != state.getBlock()) {
            ConduitNetworkManager.getInstance().disconnectFromNetwork(serverLevel, pos, state);
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        // TODO: This correctly increases the light level, but it doesn't decrease it when the
        // fluid is removed from the conduit
        // if (level.getBlockEntity(pos) instanceof FluidConduitBlockEntity fluidConduit) {
        // Fluid fluid = fluidConduit.getFluidStack().getFluid();
        // int light = fluid.getFluidType().getLightLevel();
        // TestMod.LOGGER.debug("Fluid " + fluid + " emits light " + light);
        // return fluidConduit.getFluidStack().getFluid().getFluidType().getLightLevel();
        // }
        return super.getLightEmission(state, level, pos);
    }

    @Override
    public boolean onWrenched(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, Vec3 clickLocation) {
        if (level.isClientSide()) {
            return true;
        }
        if (IWrenchable.super.onWrenched(state, level, pos, player, hand, clickLocation)) {
            return true;
        }
        // Check if the player right-clicked on the connector part of the block
        Vec3 hitPos = clickLocation.subtract(pos.getX(), pos.getY(), pos.getZ());
        // hitPos is on the surface of the block, so we can offset it slightly towards the center
        // of the block before checking if it's inside the connector
        hitPos = hitPos.subtract(0.5, 0.5, 0.5);
        hitPos = hitPos.scale(0.99);
        hitPos = hitPos.add(0.5, 0.5, 0.5);
        for (int i = 0; i < CONNECTOR_SHAPES.length; i++) {
            VoxelShape shape = CONNECTOR_SHAPES[i];
            Direction direction = Direction.values()[i];
            ConduitConnectionType conduitConnectionType = getConnectionType(level, pos, direction);
            if (conduitConnectionType.hasConnector()) {
                if (shape.bounds().contains(hitPos.x, hitPos.y, hitPos.z)) {
                    ConduitConnectionType newType = cycleConnectorType(
                            state.getValue(CONNECTOR_PROPERTIES[i]));
                    BlockState newState = state.setValue(CONNECTOR_PROPERTIES[i], newType);
                    level.setBlockAndUpdate(pos, newState);
                    player.displayClientMessage(
                            Component.translatable(
                                    TestModLanguageProvider.KEY_INFO_CONDUIT_CYCLE_MODE,
                                    Component.translatable(newType.getSerializedName())),
                            true);
                    return true;
                }
            }
        }
        return false;
    }

    public ConduitConnectionType cycleConnectorType(ConduitConnectionType type) {
        return switch (type) {
            case NONE -> ConduitConnectionType.BIDIRECTIONAL;
            case BIDIRECTIONAL -> ConduitConnectionType.PULL;
            case PULL -> ConduitConnectionType.PUSH;
            case PUSH -> ConduitConnectionType.NONE;
            case CONDUIT -> ConduitConnectionType.CONDUIT;
        };
    }

    /**
     * Check if the conduit can push payloads in the given direction.
     *
     * @param state     The block state of the conduit.
     * @param direction The direction in which the payload is being pushed.
     * @return True if the conduit can push payloads in the given direction.
     */
    public boolean canPushPayload(BlockState state, Direction direction) {
        ConduitConnectionType conduitConnectionType = state.getValue(
                CONNECTOR_PROPERTIES[direction.ordinal()]);
        return conduitConnectionType.canPushPayload();
    }

    private static VoxelShape[] shapeCache = null;

    public static final float CONDUIT_MIN = 5F / 16F;
    public static final float CONDUIT_MAX = 11F / 16F;

    public static final VoxelShape SHAPE_CONDUIT_DOWN =
            Shapes.box(CONDUIT_MIN, 0, CONDUIT_MIN, CONDUIT_MAX, CONDUIT_MIN, CONDUIT_MAX);
    public static final VoxelShape SHAPE_CONDUIT_UP =
            Shapes.box(CONDUIT_MIN, CONDUIT_MAX, CONDUIT_MIN, CONDUIT_MAX, 1, CONDUIT_MAX);
    public static final VoxelShape SHAPE_CONDUIT_NORTH =
            Shapes.box(CONDUIT_MIN, CONDUIT_MIN, 0, CONDUIT_MAX, CONDUIT_MAX, CONDUIT_MIN);
    public static final VoxelShape SHAPE_CONDUIT_SOUTH =
            Shapes.box(CONDUIT_MIN, CONDUIT_MIN, CONDUIT_MAX, CONDUIT_MAX, CONDUIT_MAX, 1);
    public static final VoxelShape SHAPE_CONDUIT_WEST =
            Shapes.box(0, CONDUIT_MIN, CONDUIT_MIN, CONDUIT_MIN, CONDUIT_MAX, CONDUIT_MAX);
    public static final VoxelShape SHAPE_CONDUIT_EAST =
            Shapes.box(CONDUIT_MAX, CONDUIT_MIN, CONDUIT_MIN, 1, CONDUIT_MAX, CONDUIT_MAX);

    public static final float CONNECTOR_MIN = 4F / 16F;
    public static final float CONNECTOR_MAX = 12F / 16F;
    public static final float CONNECTOR_THICKNESS = 4F / 16F;

    public static final VoxelShape SHAPE_BLOCK_DOWN = Shapes.box(CONNECTOR_MIN, 0, CONNECTOR_MIN,
            CONNECTOR_MAX, CONNECTOR_THICKNESS, CONNECTOR_MAX);
    public static final VoxelShape SHAPE_BLOCK_UP = Shapes.box(CONNECTOR_MIN,
            1 - CONNECTOR_THICKNESS, CONNECTOR_MIN, CONNECTOR_MAX, 1, CONNECTOR_MAX);
    public static final VoxelShape SHAPE_BLOCK_NORTH = Shapes.box(CONNECTOR_MIN, CONNECTOR_MIN, 0,
            CONNECTOR_MAX, CONNECTOR_MAX, CONNECTOR_THICKNESS);
    public static final VoxelShape SHAPE_BLOCK_SOUTH = Shapes.box(CONNECTOR_MIN, CONNECTOR_MIN,
            1 - CONNECTOR_THICKNESS, CONNECTOR_MAX, CONNECTOR_MAX, 1);
    public static final VoxelShape SHAPE_BLOCK_WEST = Shapes.box(0, CONNECTOR_MIN, CONNECTOR_MIN,
            CONNECTOR_THICKNESS, CONNECTOR_MAX, CONNECTOR_MAX);
    public static final VoxelShape SHAPE_BLOCK_EAST = Shapes.box(1 - CONNECTOR_THICKNESS,
            CONNECTOR_MIN, CONNECTOR_MIN, 1, CONNECTOR_MAX, CONNECTOR_MAX);

    public static final VoxelShape[] CONNECTOR_SHAPES =
            new VoxelShape[]{SHAPE_BLOCK_DOWN, SHAPE_BLOCK_UP, SHAPE_BLOCK_NORTH, SHAPE_BLOCK_SOUTH,
                    SHAPE_BLOCK_WEST, SHAPE_BLOCK_EAST};

    private int calculateShapeIndex(ConduitConnectionType north, ConduitConnectionType south,
            ConduitConnectionType west,
            ConduitConnectionType east, ConduitConnectionType up, ConduitConnectionType down) {
        int l = ConduitConnectionType.values().length;
        return ((((south.ordinal() * l + north.ordinal()) * l + west.ordinal()) * l
                + east.ordinal()) * l + up.ordinal()) * l + down.ordinal();
    }

    private void makeShapes() {
        if (shapeCache == null) {
            int length = ConduitConnectionType.values().length;
            shapeCache = new VoxelShape[length * length * length * length * length * length];

            // TODO: Sweet mother of nested loops!
            // Check video comments for a better way to do this
            // https://www.youtube.com/watch?v=WUhet8dOlAs&feature=youtu.be
            for (ConduitConnectionType up : ConduitConnectionType.VALUES) {
                for (ConduitConnectionType down : ConduitConnectionType.VALUES) {
                    for (ConduitConnectionType north : ConduitConnectionType.VALUES) {
                        for (ConduitConnectionType south : ConduitConnectionType.VALUES) {
                            for (ConduitConnectionType east : ConduitConnectionType.VALUES) {
                                for (ConduitConnectionType west : ConduitConnectionType.VALUES) {
                                    int idx =
                                            calculateShapeIndex(north, south, west, east, up, down);
                                    shapeCache[idx] = makeShape(north, south, west, east, up, down);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private VoxelShape makeShape(ConduitConnectionType north, ConduitConnectionType south,
            ConduitConnectionType west,
            ConduitConnectionType east, ConduitConnectionType up, ConduitConnectionType down) {
        VoxelShape shape = Shapes.box(CONDUIT_MIN, CONDUIT_MIN, CONDUIT_MIN, CONDUIT_MAX,
                CONDUIT_MAX, CONDUIT_MAX);
        shape = combineShape(shape, north, SHAPE_CONDUIT_NORTH, SHAPE_BLOCK_NORTH);
        shape = combineShape(shape, south, SHAPE_CONDUIT_SOUTH, SHAPE_BLOCK_SOUTH);
        shape = combineShape(shape, west, SHAPE_CONDUIT_WEST, SHAPE_BLOCK_WEST);
        shape = combineShape(shape, east, SHAPE_CONDUIT_EAST, SHAPE_BLOCK_EAST);
        shape = combineShape(shape, up, SHAPE_CONDUIT_UP, SHAPE_BLOCK_UP);
        shape = combineShape(shape, down, SHAPE_CONDUIT_DOWN, SHAPE_BLOCK_DOWN);
        return shape;
    }

    private VoxelShape combineShape(VoxelShape shape, ConduitConnectionType conduitConnectionType,
            VoxelShape conduitShape, VoxelShape blockShape) {
        if (conduitConnectionType == ConduitConnectionType.CONDUIT) {
            return Shapes.join(shape, conduitShape, BooleanOp.OR);
        } else if (conduitConnectionType.hasConnector()) {
            return Shapes.join(shape, Shapes.join(blockShape, conduitShape, BooleanOp.OR),
                    BooleanOp.OR);
        } else {
            return shape;
        }
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world,
            @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        ConduitConnectionType north = getConnectionType(world, pos, Direction.NORTH);
        ConduitConnectionType south = getConnectionType(world, pos, Direction.SOUTH);
        ConduitConnectionType west = getConnectionType(world, pos, Direction.WEST);
        ConduitConnectionType east = getConnectionType(world, pos, Direction.EAST);
        ConduitConnectionType up = getConnectionType(world, pos, Direction.UP);
        ConduitConnectionType down = getConnectionType(world, pos, Direction.DOWN);
        int index = calculateShapeIndex(north, south, west, east, up, down);
        return shapeCache[index];
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.getFluidTicks().schedule(
                    new ScheduledTick<>(Fluids.WATER, pos, Fluids.WATER.getTickDelay(level), 0L));
        }
        return calculateState(level, pos, state);
    }
}
