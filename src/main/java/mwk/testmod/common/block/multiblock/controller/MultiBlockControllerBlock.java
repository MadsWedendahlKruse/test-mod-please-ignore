package mwk.testmod.common.block.multiblock.controller;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
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
    
    private static final Logger LOGGER = LogUtils.getLogger();

    // The direction the controller block is facing.
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // The blueprint for the multiblock structure.
    private MultiBlockBlueprint blueprint;

    public MultiBlockControllerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Set the direction the controller block is facing.
        return defaultBlockState().setValue(FACING, 
            context.getHorizontalDirection().getOpposite());
    }

    /**
     * Set the blueprint for the multiblock structure.
     * This has to be done outside the constructor because the blueprint requires the 
     * controller block to be initialized. Otherwise we would have a circular dependency.
     * 
     * @param blueprint The blueprint for the multiblock structure.
     */
    public void setBlueprint(MultiBlockBlueprint blueprint) {
        this.blueprint = blueprint;
    }

    /**
     * Set the formed state of the multiblock structure.
     * 
     * @param level The level.
     * @param controllerPos The position of the controller block.
     * @param controllerState The state of the controller block.
     * @param isFormed Whether or not the multiblock structure is formed.
     * @return Whether or not the multiblock structure is formed.
     */
    public boolean setMultiblockFormed(Level level, BlockPos controllerPos, 
        BlockState controllerState, boolean isFormed) {
        System.out.println("setMultiblockFormed");
        // Get the positions of the blocks in the blueprint.
        BlockPos[] positions = blueprint.getRotatedPositions(controllerState.getValue(FACING));
        // Check that the blocks are not already in the correct state.
        // This would imply that they are part of a different multiblock structure.
        for (BlockPos position : positions) {
            BlockPos blockPos = controllerPos.offset(position);
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.getBlock() instanceof MultiBlockPartBlock) {
                if (blockState.getValue(IS_FORMED) == isFormed) {
                    System.out.println("Block.is_formed @ " + blockPos + " is already " + 
                        isFormed);
                    return false;
                }
            }
        }
        // Set the formed state of the blocks.
        for (BlockPos position : positions) {
            BlockPos blockPos = controllerPos.offset(position);
            // If the block is the controller block, we don't want to set the controller
            // position to itself. This is to avoid infinite recursion when use is called.
            if (blockPos.equals(controllerPos)) {
                this.setFormed(level, blockPos, controllerState, isFormed, null);
                continue;
            }
            BlockState blockState = level.getBlockState(blockPos);
            // Not sure if this check will ever fail
            if (blockState.getBlock() instanceof MultiBlockPartBlock) {
                ((MultiBlockPartBlock) blockState.getBlock()).setFormed(
                    level, blockPos, blockState, isFormed, controllerPos);
            } else {
                System.out.println("Block @ " + blockPos + " is " + blockState.getBlock() + 
                    ", but expected instanceof MultiBlockPartBlock");
                return false;
            }
        }
        System.out.println("successfully set multiblock formed to " + isFormed);
        return true;
    }

    /**
     * Toggle the multiblock structure.
     * 
     * @param level The level.
     * @param controllerPos The position of the controller block.
     * @param controllerState The state of the controller block.
     * @return Whether or not the multiblock structure was toggled.
     */
    public boolean toggleMultiblock(Level level, BlockPos controllerPos, 
        BlockState controllerState) {
        return setMultiblockFormed(level, controllerPos, controllerState, 
            !controllerState.getValue(IS_FORMED));
    }   

    @Override
    public InteractionResult use(
        BlockState state, Level level, BlockPos pos, Player player, 
        InteractionHand hand, BlockHitResult hit) {
        // Check if the player is holding a wrench (TEMP: stick instead).
        if (player.getItemInHand(hand).getItem() == Items.STICK) {
            // Check if the blueprint has been set correctly.
            if (blueprint == null) {
                // TODO: Exception?
                LOGGER.error("Blueprint has not been set!");
                return InteractionResult.FAIL;
            }
            // Check if the mutliblock structure can be formed.
            if (blueprint.isValid(level, pos, state)) {
                // TODO: Print in chat?
                // Toggle the multiblock structure.
                if (toggleMultiblock(level, pos, state)) {
                    return InteractionResult.SUCCESS;
                }
            } else {
                // TODO: Print in chat?
                return InteractionResult.FAIL;
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }
}
