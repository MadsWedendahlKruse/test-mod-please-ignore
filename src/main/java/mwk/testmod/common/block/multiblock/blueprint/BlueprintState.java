package mwk.testmod.common.block.multiblock.blueprint;

import java.util.ArrayList;
import mwk.testmod.common.block.multiblock.controller.MultiBlockControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/**
 * Helper class for storing and checking the state of a blueprint in the world.
 */
public class BlueprintState {

    private MultiBlockBlueprint blueprint;
    private Level level;
    private BlockPos controllerPos;
    private Direction controllerFacing;

    private ArrayList<BlueprintBlockInfo> emptyBlocks;
    private ArrayList<BlueprintBlockInfo> missingBlocks;
    private ArrayList<BlueprintBlockInfo> incorrectBlocks;

    public BlueprintState(MultiBlockBlueprint blueprint, Level level, BlockPos controllerPos) {
        this.blueprint = blueprint;
        this.level = level;
        this.controllerPos = controllerPos;
        this.controllerFacing =
                level.getBlockState(controllerPos).getValue(MultiBlockControllerBlock.FACING);

        emptyBlocks = new ArrayList<>();
        missingBlocks = new ArrayList<>();
        incorrectBlocks = new ArrayList<>();

        checkBlocks();
    }

    private void checkBlocks() {
        for (BlueprintBlockInfo blockInfo : blueprint.getBlocks()) {
            BlockPos absolutePos = blockInfo.getAbsolutePosition(controllerPos, controllerFacing);
            blockInfo.setActualState(level.getBlockState(absolutePos));

            if (blockInfo.isBlockEmpty()) {
                emptyBlocks.add(blockInfo);
            }
            if (blockInfo.isBlockMissing()) {
                missingBlocks.add(blockInfo);
            }
            if (blockInfo.isBlockIncorrect()) {
                incorrectBlocks.add(blockInfo);
            }
        }
    }

    /**
     * Updates the state of the blueprint. This checks the state of all blocks in the blueprint and
     * updates the lists of empty, missing and incorrect blocks.
     */
    public void update() {
        emptyBlocks.clear();
        missingBlocks.clear();
        incorrectBlocks.clear();

        checkBlocks();
    }

    /**
     * @return true if the blueprint is complete and ready to be activated, false otherwise.
     */
    public boolean isComplete() {
        return missingBlocks.isEmpty();
    }

    /**
     * @return the list of blocks that are empty in the world.
     */
    public ArrayList<BlueprintBlockInfo> getEmptyBlocks() {
        return emptyBlocks;
    }

    /**
     * @return the list of blocks that are missing in the world.
     */
    public ArrayList<BlueprintBlockInfo> getMissingBlocks() {
        return missingBlocks;
    }

    /**
     * @return the list of blocks that are incorrect in the world.
     */
    public ArrayList<BlueprintBlockInfo> getIncorrectBlocks() {
        return incorrectBlocks;
    }
}
