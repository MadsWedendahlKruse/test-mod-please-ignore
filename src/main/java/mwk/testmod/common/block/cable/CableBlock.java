package mwk.testmod.common.block.cable;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.cable.network.CableNetworkManager;
import mwk.testmod.common.block.interfaces.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Almost all of this is taken from https://www.mcjty.eu/docs/1.20.4_neo/ep5#introduction
 */
public class CableBlock extends Block implements EntityBlock, IWrenchable, SimpleWaterloggedBlock {

    // Same order as Direction.values()
    public static final EnumProperty<ConnectorType> DOWN =
            EnumProperty.create("down", ConnectorType.class);
    public static final EnumProperty<ConnectorType> UP =
            EnumProperty.create("up", ConnectorType.class);
    public static final EnumProperty<ConnectorType> NORTH =
            EnumProperty.create("north", ConnectorType.class);
    public static final EnumProperty<ConnectorType> SOUTH =
            EnumProperty.create("south", ConnectorType.class);
    public static final EnumProperty<ConnectorType> WEST =
            EnumProperty.create("west", ConnectorType.class);
    public static final EnumProperty<ConnectorType> EAST =
            EnumProperty.create("east", ConnectorType.class);
    public static final EnumProperty<ConnectorType>[] CONNECTOR_PROPERTIES =
            new EnumProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};

    public CableBlock(Properties properties) {
        super(properties.noOcclusion());
        makeShapes();
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
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
        return new CableBlockEntity(pos, state);
    }

    /**
     * @return The connector type of the block at the given position.
     */
    private ConnectorType getConnectorType(BlockGetter world, BlockPos connectorPos,
            Direction facing) {
        BlockPos pos = connectorPos.relative(facing);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof CableBlock) {
            return ConnectorType.CABLE;
        } else if (isConnectable(world, connectorPos, facing)) {
            return ConnectorType.BLOCK;
        } else {
            return ConnectorType.NONE;
        }
    }

    /**
     * Check if the block at the given position is connectable to the cable.
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
        IEnergyStorage energyStorage = blockEntity.getLevel()
                .getCapability(Capabilities.EnergyStorage.BLOCK, pos, facing.getOpposite());
        if (energyStorage == null) {
            return false;
        }
        return true;
    }

    @Nonnull
    public BlockState calculateState(LevelAccessor world, BlockPos pos, BlockState state) {
        ArrayList<ConnectorType> connectors = new ArrayList<>(Direction.values().length);
        for (Direction facing : Direction.values()) {
            connectors.add(getConnectorType(world, pos, facing));
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
            CableNetworkManager.getInstance().connectToNetwork(serverLevel, pos, state);
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
            CableNetworkManager.getInstance().disconnectFromNetwork(serverLevel, pos, state);
        }
    }

    private static VoxelShape[] shapeCache = null;

    private static final double CABLE_MIN = 5. / 16.;
    private static final double CABLE_MAX = 11. / 16.;

    private static final VoxelShape SHAPE_CABLE_NORTH =
            Shapes.box(CABLE_MIN, CABLE_MIN, 0, CABLE_MAX, CABLE_MAX, CABLE_MIN);
    private static final VoxelShape SHAPE_CABLE_SOUTH =
            Shapes.box(CABLE_MIN, CABLE_MIN, CABLE_MAX, CABLE_MAX, CABLE_MAX, 1);
    private static final VoxelShape SHAPE_CABLE_WEST =
            Shapes.box(0, CABLE_MIN, CABLE_MIN, CABLE_MIN, CABLE_MAX, CABLE_MAX);
    private static final VoxelShape SHAPE_CABLE_EAST =
            Shapes.box(CABLE_MAX, CABLE_MIN, CABLE_MIN, 1, CABLE_MAX, CABLE_MAX);
    private static final VoxelShape SHAPE_CABLE_UP =
            Shapes.box(CABLE_MIN, CABLE_MAX, CABLE_MIN, CABLE_MAX, 1, CABLE_MAX);
    private static final VoxelShape SHAPE_CABLE_DOWN =
            Shapes.box(CABLE_MIN, 0, CABLE_MIN, CABLE_MAX, CABLE_MIN, CABLE_MAX);

    private static final VoxelShape SHAPE_BLOCK_NORTH = Shapes.box(.2, .2, 0, .8, .8, .1);
    private static final VoxelShape SHAPE_BLOCK_SOUTH = Shapes.box(.2, .2, .9, .8, .8, 1);
    private static final VoxelShape SHAPE_BLOCK_WEST = Shapes.box(0, .2, .2, .1, .8, .8);
    private static final VoxelShape SHAPE_BLOCK_EAST = Shapes.box(.9, .2, .2, 1, .8, .8);
    private static final VoxelShape SHAPE_BLOCK_UP = Shapes.box(.2, .9, .2, .8, 1, .8);
    private static final VoxelShape SHAPE_BLOCK_DOWN = Shapes.box(.2, 0, .2, .8, .1, .8);

    private int calculateShapeIndex(ConnectorType north, ConnectorType south, ConnectorType west,
            ConnectorType east, ConnectorType up, ConnectorType down) {
        int l = ConnectorType.values().length;
        return ((((south.ordinal() * l + north.ordinal()) * l + west.ordinal()) * l
                + east.ordinal()) * l + up.ordinal()) * l + down.ordinal();
    }

    private void makeShapes() {
        if (shapeCache == null) {
            int length = ConnectorType.values().length;
            shapeCache = new VoxelShape[length * length * length * length * length * length];

            // TODO: Sweet mother of nested loops!
            // Check video comments for a better way to do this
            // https://www.youtube.com/watch?v=WUhet8dOlAs&feature=youtu.be
            for (ConnectorType up : ConnectorType.VALUES) {
                for (ConnectorType down : ConnectorType.VALUES) {
                    for (ConnectorType north : ConnectorType.VALUES) {
                        for (ConnectorType south : ConnectorType.VALUES) {
                            for (ConnectorType east : ConnectorType.VALUES) {
                                for (ConnectorType west : ConnectorType.VALUES) {
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

    private VoxelShape makeShape(ConnectorType north, ConnectorType south, ConnectorType west,
            ConnectorType east, ConnectorType up, ConnectorType down) {
        VoxelShape shape =
                Shapes.box(CABLE_MIN, CABLE_MIN, CABLE_MIN, CABLE_MAX, CABLE_MAX, CABLE_MAX);
        shape = combineShape(shape, north, SHAPE_CABLE_NORTH, SHAPE_BLOCK_NORTH);
        shape = combineShape(shape, south, SHAPE_CABLE_SOUTH, SHAPE_BLOCK_SOUTH);
        shape = combineShape(shape, west, SHAPE_CABLE_WEST, SHAPE_BLOCK_WEST);
        shape = combineShape(shape, east, SHAPE_CABLE_EAST, SHAPE_BLOCK_EAST);
        shape = combineShape(shape, up, SHAPE_CABLE_UP, SHAPE_BLOCK_UP);
        shape = combineShape(shape, down, SHAPE_CABLE_DOWN, SHAPE_BLOCK_DOWN);
        return shape;
    }

    private VoxelShape combineShape(VoxelShape shape, ConnectorType connectorType,
            VoxelShape cableShape, VoxelShape blockShape) {
        if (connectorType == ConnectorType.CABLE) {
            return Shapes.join(shape, cableShape, BooleanOp.OR);
        } else if (connectorType == ConnectorType.BLOCK) {
            return Shapes.join(shape, Shapes.join(blockShape, cableShape, BooleanOp.OR),
                    BooleanOp.OR);
        } else {
            return shape;
        }
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world,
            @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        ConnectorType north = getConnectorType(world, pos, Direction.NORTH);
        ConnectorType south = getConnectorType(world, pos, Direction.SOUTH);
        ConnectorType west = getConnectorType(world, pos, Direction.WEST);
        ConnectorType east = getConnectorType(world, pos, Direction.EAST);
        ConnectorType up = getConnectorType(world, pos, Direction.UP);
        ConnectorType down = getConnectorType(world, pos, Direction.DOWN);
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
        if (!level.isClientSide()) {
            CableNetworkManager.getInstance().updateNetworks(pos, state, neighborState, direction);
        }
        return calculateState(level, pos, state);
    }
}
