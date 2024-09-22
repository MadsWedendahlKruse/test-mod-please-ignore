package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.util.inventory.IOUtils;
import mwk.testmod.common.util.inventory.handler.InputItemHandler;
import mwk.testmod.common.util.inventory.handler.OutputItemHandler;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public class MultiBlockItemIOPortBlockEntity extends MultiBlockIOPortBlockEntity {

    public MultiBlockItemIOPortBlockEntity(BlockPos pos, BlockState state, boolean input) {
        super(input ? TestModBlockEntities.MULTI_ITEM_INPUT_PORT_ENTITY_TYPE.get()
                : TestModBlockEntities.MULTI_ITEM_OUTPUT_PORT_ENTITY_TYPE.get(), pos, state, input);
    }

    @Override
    public IItemHandler getItemHandler(Direction direction) {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof MachineBlockEntity machine) {
                return input ? machine.getInputItemHandler(direction, false)
                        : machine.getOutputItemHandler(direction);
            }
        }
        return null;
    }

    @Override
    protected void pullInput(Level level, MachineBlockEntity machine, BlockPos pos) {
        InputItemHandler inputHandler = machine.getInputItemHandler(null, false);
        IOUtils.pullItemInput(level, inputHandler, pos, inputHandler.getStartSlot(),
                inputHandler.getEndSlot(), 64);
    }

    @Override
    protected void pushOutput(Level level, MachineBlockEntity machine, BlockPos pos) {
        OutputItemHandler outputHandler = machine.getOutputItemHandler(null);
        IOUtils.pushItemOutput(level, outputHandler, pos, outputHandler.getStartSlot(),
                outputHandler.getEndSlot(), 64);
    }
}
