package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.interfaces.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockIOPortBlockEntity extends MultiBlockPartBlockEntity implements ITickable {

    protected final boolean input;
    private final InputPuller inputPuller;
    private final OutputEjector outputEjector;

    public MultiBlockIOPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            boolean input, InputPuller inputPuller, OutputEjector outputEjector) {
        super(type, pos, state);
        this.input = input;
        this.inputPuller = inputPuller;
        this.outputEjector = outputEjector;
    }

    @Override
    public void tick() {
        if (!isFormed()) {
            return;
        }
        BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
        if (controllerEntity instanceof MachineBlockEntity machine) {
            if (input) {
                inputPuller.pullInput(machine, this.worldPosition);
            } else {
                outputEjector.ejectOutput(machine, this.worldPosition);
            }
        }
    }

    @FunctionalInterface
    public interface InputPuller {
        void pullInput(MachineBlockEntity machine, BlockPos pos);
    }

    @FunctionalInterface
    public interface OutputEjector {
        void ejectOutput(MachineBlockEntity machine, BlockPos pos);
    }
}
