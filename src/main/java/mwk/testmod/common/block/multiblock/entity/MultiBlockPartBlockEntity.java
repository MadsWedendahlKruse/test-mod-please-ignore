package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.SuperFurnaceBlockEntity;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * A block entity for a block that is part of a multiblock structure.
 */
public class MultiBlockPartBlockEntity extends BlockEntity {

    // The position of the controller block for this multiblock structure.
    private BlockPos controllerPos;

    public MultiBlockPartBlockEntity(BlockPos pos, BlockState state) {
        this(TestModBlockEntities.MULTI_BLOCK_PART_BLOCK_ENTITY_TYPE.get(), pos, state);
    }

    public MultiBlockPartBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        // Mark the block entity as changed so that the game saves it
        setChanged();
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public boolean hasController() {
        return controllerPos != null;
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

    protected boolean isFormed() {
        if (hasController()) {
            return isFormed(controllerPos);
        }
        return false;
    }

    protected boolean isFormed(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof MultiBlockPartBlock) {
            return state.getValue(MultiBlockPartBlock.FORMED);
        }
        return false;
    }

    public IEnergyStorage getEnergyHandler() {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof SuperFurnaceBlockEntity energyEntity) {
                return energyEntity.getEnergyHandler();
            }
        }
        return null;
    }

    public IItemHandler getItemHandler() {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof SuperFurnaceBlockEntity itemEntity) {
                return itemEntity.getItemHandler();
            }
        }
        return null;
    }
}