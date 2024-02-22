package mwk.testmod.common.block.multiblock.controller;

import mwk.testmod.TestMod;
import mwk.testmod.client.hologram.HologramRenderer;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintBlockInfo;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintState;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The controller block of a multiblock structure.
 */
public class MultiBlockControllerBlock extends MultiBlockPartBlock {

    // The direction the controller block is facing.
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // The blueprint for the multiblock structure.
    private MultiBlockBlueprint blueprint;

    public MultiBlockControllerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Set the direction the controller block is facing.
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /**
     * Set the blueprint for the multiblock structure. This has to be done outside the constructor
     * because the blueprint requires the controller block to be initialized. Otherwise we would
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
     * @param level The level.
     * @param controllerPos The position of the controller block.
     * @param controllerState The state of the controller block.
     * @param isFormed The desired formed state of the multiblock structure.
     * @param checkBlueprint Whether or not to check if the multiblock structure matches the
     *        blueprint. When a block in the multiblock structure is broken, the block might already
     *        be gone before we get to this method. In that case we don't want to check the
     *        blueprint, because it will fail, and instead we want to ensure that the multiblock
     *        structure is unformed.
     * @return Whether or not the multiblock structure is formed.
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
                if (blockState.getValue(IS_FORMED) == isFormed) {
                    TestMod.LOGGER
                            .info("Block.is_formed @ " + blockPos + " is already " + isFormed);
                    return false;
                }
            }
        }
        // Set the formed state of the blocks.
        for (BlockPos blockPos : positions) {
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.getBlock() instanceof MultiBlockPartBlock) {
                ((MultiBlockPartBlock) blockState.getBlock()).setPartFormed(level, blockPos,
                        blockState, isFormed, controllerPos);
            } else {
                if (checkBlueprint) {
                    TestMod.LOGGER.debug("Block @ " + blockPos + " is " + blockState.getBlock()
                            + ", but expected instanceof MultiBlockPartBlock");
                    return false;
                }
                continue;
            }
        }
        TestMod.LOGGER.debug("successfully set multiblock formed to " + isFormed);
        return true;
    }

    /**
     * Toggle the multiblock structure.
     * 
     * @param level The level.
     * @param controllerPos The position of the controller block.
     * @param controllerState The state of the controller block.
     * @param checkBlueprint Whether or not to check if the multiblock structure matches the
     *        blueprint.
     * @return Whether or not the multiblock structure was toggled.
     */
    public boolean toggleMultiblock(Level level, BlockPos controllerPos,
            BlockState controllerState) {
        return setMultiblockFormed(level, controllerPos, controllerState,
                !controllerState.getValue(IS_FORMED), true);
    }

    @Override
    public boolean onWrenched(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand) {
        if (super.onWrenched(state, level, pos, player, hand)) {
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
                return true;
            }
        } else {
            if (!state.getValue(IS_FORMED)) {
                HologramRenderer.getInstance().toggleController(level, pos);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState,
            boolean movedByPiston) {
        TestMod.LOGGER.debug("MultiBlockControllerBlock::onRemove");
        // TODO: This doesn't do anything if we also check for client side
        // Does onRemove only run on the server side?
        if (HologramRenderer.getInstance().isCurrentController(pos)) {
            HologramRenderer.getInstance().clearController();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private InteractionResult attemptBuildMultiBlock(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        Inventory inventory = player.getInventory();
        BlueprintState blueprintState = blueprint.getState(level, pos);
        if (blueprintState.isComplete()) {
            player.displayClientMessage(
                    Component.translatable("info.testmod.controller.blueprint.complete"), true);
            return InteractionResult.FAIL;
        }
        boolean playerHasMissingBlock = false;
        for (BlueprintBlockInfo blockInfo : blueprintState.getMissingBlocks()) {
            BlockPos blockInfoPos = blockInfo.getAbsolutePosition(pos, state.getValue(FACING));
            ItemStack stack = blockInfo.getExpectedItemStack();
            int itemIndex = inventory.findSlotMatchingItem(stack);
            if (itemIndex != -1) {
                playerHasMissingBlock = true;
                // Sanity check that we're not overwriting a block that's already there.
                if (!level.getBlockState(blockInfoPos).isAir()) {
                    player.displayClientMessage(
                            Component.translatable("info.testmod.controller.blueprint.blocked"),
                            true);
                    return InteractionResult.SUCCESS;
                }
                // Simulate the block being placed by the player. This handles playing the sound
                // and removing the item from the player's inventory.
                if (inventory.getItem(itemIndex).getItem() instanceof BlockItem blockItem) {
                    BlockHitResult hitResult = new BlockHitResult(hit.getLocation(),
                            hit.getDirection(), blockInfoPos, hit.isInside());
                    BlockPlaceContext context =
                            new BlockPlaceContext(player, hand, stack, hitResult);
                    // Notify the blueprint hologram that a block has been placed.
                    // Would be cool if this happened automatically. but
                    // BlockEvent.EntityPlaceEvent only gets fired when an entity
                    // places a block, and there's not another alternative.
                    TestMod.ClientForgeEvents.checkHologramUpdate(blockInfoPos);
                    return blockItem.place(context);
                }
            }
        }
        // Notify the player if they don't have any of the missing blocks in their inventory.
        if (!playerHasMissingBlock) {
            player.displayClientMessage(
                    Component.translatable("info.testmod.controller.blueprint.insufficient_blocks"),
                    true);
            // TODO: Not sure if we want to return SUCCESS here?
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        // Right clicking with a wrench is handled by Wrenchable#onWrenched.
        // (so skip it here)
        if (player.getItemInHand(hand).getItem() == TestModItems.WRENCH_ITEM.get()) {
            return InteractionResult.PASS;
        }
        // Explain to the player how to view the blueprint.
        if (!HologramRenderer.getInstance().isCurrentController(pos)
                && !state.getValue(IS_FORMED)) {
            player.displayClientMessage(
                    Component.translatable("info.testmod.controller.blueprint.show"), true);
            return InteractionResult.SUCCESS;
        }
        if (HologramRenderer.getInstance().isCurrentController(pos) && !state.getValue(IS_FORMED)) {
            return attemptBuildMultiBlock(state, level, pos, player, hand, hit);
        }
        return super.use(state, level, pos, player, hand, hit);
    }
}
