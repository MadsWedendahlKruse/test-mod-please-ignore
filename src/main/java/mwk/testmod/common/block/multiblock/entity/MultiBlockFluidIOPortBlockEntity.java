package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.util.inventory.IOUtils;
import mwk.testmod.common.util.inventory.handler.InputFluidHandler;
import mwk.testmod.common.util.inventory.handler.OutputFluidHandler;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MultiBlockFluidIOPortBlockEntity extends MultiBlockIOPortBlockEntity {

    public MultiBlockFluidIOPortBlockEntity(BlockPos pos, BlockState state, boolean input) {
        super(input ? TestModBlockEntities.MULTI_FLUID_INPUT_PORT_ENTITY_TYPE.get()
                        : TestModBlockEntities.MULTI_FLUID_OUTPUT_PORT_ENTITY_TYPE.get(), pos, state,
                input);
    }

    @Override
    public IFluidHandler getFluidHandler(Direction direction) {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof MachineBlockEntity machine) {
                IFluidHandler handler = input ? machine.getInputFluidHandler(direction)
                        : machine.getOutputFluidHandler(direction);
                return handler;
            }
        }
        return null;
    }

    @Override
    protected void pullInput(Level level, MachineBlockEntity machine, BlockPos pos) {
        InputFluidHandler inputHandler = machine.getInputFluidHandler(null);
        IOUtils.pullFluidInput(level, inputHandler, pos, inputHandler.getStartTank(),
                inputHandler.getEndTank(), 1000);
    }

    @Override
    protected void pushOutput(Level level, MachineBlockEntity machine, BlockPos pos) {
        OutputFluidHandler outputHandler = machine.getOutputFluidHandler(null);
        IOUtils.pushFluidOutput(level, outputHandler, pos, outputHandler.getStartTank(),
                outputHandler.getEndTank(), 1000);
    }
}
