package mwk.testmod.common.block.multiblock;

import mwk.testmod.TestMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block entity for a block that is part of a multiblock structure.
 */
public class MultiBlockPartBlockEntity extends BlockEntity {
   
    // The position of the controller block for this multiblock structure.
    private BlockPos controllerPos;

    public MultiBlockPartBlockEntity(BlockPos pos, BlockState state) {
        super(TestMod.MULTI_BLOCK_PART_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        // Mark the block entity as changed so that the game saves it
        setChanged();
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (controllerPos != null) {
            tag.putLong("controllerPos", controllerPos.asLong());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("controllerPos")) {
            controllerPos = BlockPos.of(tag.getLong("controllerPos"));
        }
    }
}
