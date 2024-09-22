package mwk.testmod.common.block.multiblock;

import java.util.function.BiFunction;
import mwk.testmod.TestMod;
import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.client.render.hologram.events.ClearIfCurrentEvent;
import mwk.testmod.client.render.hologram.events.WrenchEvent;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockUtils;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The controller block of a multiblock structure.
 */
public class MultiBlockControllerBlock extends MultiBlockPartBlock {

    // The direction the controller block is facing.
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    // The working state of the multiblock structure.
    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    // The blueprint for the multiblock structure.
    private MultiBlockBlueprint blueprint;
    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory;

    public MultiBlockControllerBlock(Properties properties,
            BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory) {
        super(properties);
        registerDefaultState(
                defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WORKING, false));
        this.blockEntityFactory = blockEntityFactory;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(WORKING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return blockEntityFactory.apply(pPos, pState);
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
     * Get the shape of the multiblock structure when it is formed.
     *
     * @param state The state of the controller block. This is only used to get the direction the
     *              controller block is facing, and does not check if the multiblock structure is
     *              formed.
     * @return The shape of the multiblock structure when it is formed.
     */
    public VoxelShape getFormedShape(BlockState state) {
        // TODO: This should be cached. It should also be a proper box for each block, not just a
        // bounding box, so that we can have more complex shapes.
        if (blueprint != null) {
//            AABB aabb = blueprint.getAABB(null, state.getValue(FACING));
//            return Block.box(aabb.minX * 16, aabb.minY * 16, aabb.minZ * 16, aabb.maxX * 16,
//                    aabb.maxY * 16, aabb.maxZ * 16);
            return blueprint.getShape(state.getValue(FACING));
        }
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        if (state.getValue(FORMED)) {
            VoxelShape formedShape = getFormedShape(state);
            if (formedShape != null) {
                return formedShape;
            }
        }
        // TODO: Dirty fix for faces of the model becoming invisible when a block is placed
        // next to the controller block.
        final double OFFSET = 0.001;
        return Block.box(0 + OFFSET, 0 + OFFSET, 0 + OFFSET, 16 - OFFSET, 16 - OFFSET, 16 - OFFSET);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    /**
     * Set the blueprint for the multiblock structure. This has to be done outside the constructor
     * because the blueprint requires the controller block to be initialized, otherwise we would
     * have a circular dependency.
     *
     * @param blueprint The blueprint for the multiblock structure.
     */
    public void setBlueprint(MultiBlockBlueprint blueprint) {
        this.blueprint = blueprint;
    }

    /**
     * @return The blueprint for the multiblock structure.
     */
    public MultiBlockBlueprint getBlueprint() {
        return blueprint;
    }

    /**
     * Set the formed state of the multiblock structure.
     *
     * @param level           The level.
     * @param controllerPos   The position of the controller block.
     * @param controllerState The state of the controller block.
     * @param isFormed        The desired formed state of the multiblock structure.
     * @param checkBlueprint  Whether to check if the multiblock structure matches the blueprint.
     *                        When a block in the multiblock structure is broken, the block might
     *                        already be gone before we get to this method. In that case we don't
     *                        want to check the blueprint, because it will fail, and instead we want
     *                        to ensure that the multiblock structure is unformed.
     * @return Whether the multiblock structure is formed.
     */
    public boolean setMultiblockFormed(Level level, BlockPos controllerPos,
            BlockState controllerState, boolean isFormed, boolean checkBlueprint) {
        TestMod.LOGGER.debug("setMultiblockFormed");
        BlockPos[] positions =
                blueprint.getAbsolutePositions(controllerPos, controllerState.getValue(FACING));
        // Check that the blocks are not already in the correct state.
        // This would imply that they are part of a different multiblock structure.
        for (BlockPos blockPos : positions) {
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.getBlock() instanceof MultiBlockPartBlock) {
                if (blockState.getValue(FORMED) == isFormed) {
                    TestMod.LOGGER.info("Block.formed @ " + blockPos + " is already " + isFormed);
                    return false;
                }
            }
        }
        // Set the formed state of the blocks.
        for (BlockPos blockPos : positions) {
            BlockState blockState = level.getBlockState(blockPos);
            if (blockPos == controllerPos) {
                blockState = controllerState;
            }
            if (blockState.getBlock() instanceof MultiBlockPartBlock partBlock) {
                partBlock.setPartFormed(level, blockPos, blockState, isFormed, controllerPos);
            } else {
                if (checkBlueprint) {
                    TestMod.LOGGER.debug("Block @ " + blockPos + " is " + blockState.getBlock()
                            + ", but expected instanceof MultiBlockPartBlock");
                    return false;
                }
            }
        }
        TestMod.LOGGER.debug("successfully set multiblock formed to " + isFormed);
        return true;
    }

    /**
     * Toggle the multiblock structure.
     *
     * @param level           The level.
     * @param controllerPos   The position of the controller block.
     * @param controllerState The state of the controller block.
     * @return Whether the multiblock structure was toggled.
     */
    public boolean toggleMultiblock(Level level, BlockPos controllerPos,
            BlockState controllerState) {
        return setMultiblockFormed(level, controllerPos, controllerState,
                !controllerState.getValue(FORMED), true);
    }

    private void dissasembleMultiblock(Level level, BlockPos controllerPos,
            BlockState controllerState) {
        BlockPos[] positions =
                blueprint.getAbsolutePositions(controllerPos, controllerState.getValue(FACING));
        for (BlockPos blockPos : positions) {
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.getBlock() instanceof MultiBlockPartBlock) {
                // Deliberately not using level#destroyBlock to avoid spawning particles.
                // TODO: Identical to IWrenchable#onWrenched. Maybe refactor?
                level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        new ItemStack(blockState.getBlock().asItem()));
            }
        }
    }

    @Override
    public boolean onWrenched(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, Vec3 clickLocation) {
        if (state.getValue(FORMED)) {
            if (player.isShiftKeyDown()) {
                dissasembleMultiblock(level, pos, state);
                return true;
            }
        }
        if (super.onWrenched(state, level, pos, player, hand, clickLocation)) {
            return true;
        }
        // TODO: Presumably this is only called when the player has a wrench in their
        // hand, so maybe we don't have to check if the player is holding a wrench?
        if (blueprint == null) {
            // TODO: Exception?
            TestMod.LOGGER.error("Blueprint has not been set!");
            return false;
        }
        if (blueprint.isComplete(level, pos)) {
            if (toggleMultiblock(level, pos, state)) {
                if (level.isClientSide()) {
                    HologramRenderer.getInstance().setEvent(
                            new ClearIfCurrentEvent(blueprint, pos, state.getValue(FACING)));
                }
                return true;
            }
        } else {
            if (!state.getValue(FORMED) && level.isClientSide()) {
                HologramRenderer.getInstance().setEvent(new WrenchEvent(level, pos));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState,
            boolean movedByPiston) {
        TestMod.LOGGER.debug("MultiBlockControllerBlock::onRemove");
        // Note to self: this method runs every time the block state changes, not just when the
        // block is broken (contrary to what the name might suggest).
        if (newState.getBlock() instanceof MultiBlockControllerBlock
                && newState.getValue(FORMED) == state.getValue(FORMED)) {
            return;
        }
        HologramRenderer.getInstance()
                .setEvent(new ClearIfCurrentEvent(blueprint, pos, state.getValue(FACING)));
        if (level.getBlockEntity(pos) instanceof MachineBlockEntity blockEntity) {
            Containers.dropContents(level, pos, blockEntity.getDrops());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        // Right-clicking with a wrench is handled by Wrenchable#onWrenched.
        // (so skip it here)
        if (player.getItemInHand(hand).getItem() == TestModItems.WRENCH_ITEM.get()) {
            return InteractionResult.PASS;
        }
        boolean isCurrentBlueprint = HologramRenderer.getInstance().isCurrentBlueprint(pos,
                blueprint, state.getValue(FACING));
        boolean isFormed = state.getValue(FORMED);
        if (!isFormed) {
            // If the player right-clicks the controller block...
            if (!isCurrentBlueprint) {
                // ...and it's not the current blueprint, explain how to view the blueprint.
                player.displayClientMessage(Component.translatable(
                        TestModLanguageProvider.KEY_INFO_CONTROLLER_BLUEPRINT_HELP), true);
            } else {
                // ...and it is the current blueprint, attempt to build the multiblock structure.
                return MultiBlockUtils.attemptBuildMultiBlock(level, blueprint, pos,
                        state.getValue(FACING), player, hand, true);
            }
        } else {
            // Open the menu if the multiblock structure is formed.
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                TestMod.LOGGER.debug("Opening menu for " + blockEntity);
                if (blockEntity instanceof MenuProvider menuProvider) {
                    serverPlayer.openMenu(menuProvider, buf -> {
                        buf.writeBlockPos(pos);
                    });
                } else {
                    throw new IllegalStateException("Our named container provider is missing!");
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.use(state, level, pos, player, hand, hit);
    }
}
