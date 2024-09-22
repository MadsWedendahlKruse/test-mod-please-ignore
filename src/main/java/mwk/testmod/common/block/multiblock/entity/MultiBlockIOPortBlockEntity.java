package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.interfaces.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block entity for a multi-block IO port.
 */
public abstract class MultiBlockIOPortBlockEntity extends MultiBlockPartBlockEntity implements
        ITickable {

    protected final boolean input;

    public MultiBlockIOPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            boolean input) {
        super(type, pos, state);
        this.input = input;
    }

    @Override
    public void tick() {
        if (!isFormed()) {
            return;
        }
        BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
        if (controllerEntity instanceof MachineBlockEntity machine) {
            if (input) {
                if (machine.isAutoPull()) {
                    pullInput(level, machine, this.worldPosition);
                }
            } else {
                if (machine.isAutoPush()) {
                    pushOutput(level, machine, this.worldPosition);
                }
            }
        }
    }

    protected abstract void pullInput(Level level, MachineBlockEntity machine, BlockPos pos);

    protected abstract void pushOutput(Level level, MachineBlockEntity machine, BlockPos pos);
}
