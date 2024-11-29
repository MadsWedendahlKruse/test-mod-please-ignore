package mwk.testmod.common.block.multiblock.entity.ports;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.entity.modules.InventoryModule;
import mwk.testmod.common.util.inventory.IOUtils;
import mwk.testmod.common.util.handlers.InputItemHandler;
import mwk.testmod.common.util.handlers.OutputItemHandler;
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
            if (controllerEntity instanceof MachineBlockEntity machine
                    && machine.inventory().isPresent()) {
                InventoryModule inventory = machine.inventory().get();
                return input ? inventory.getInputItemHandler(direction, false)
                        : inventory.getOutputItemHandler(direction);
            }
        }
        return null;
    }

    @Override
    protected void pullInput(Level level, MachineBlockEntity machine, BlockPos pos) {
        if (machine.inventory().isEmpty()) {
            return;
        }
        InventoryModule inventory = machine.inventory().get();
        InputItemHandler inputHandler = inventory.getInputItemHandler(null, false);
        IOUtils.pullItemInput(level, inputHandler, pos, inputHandler.getStartSlot(),
                inputHandler.getEndSlot(), 64);
    }

    @Override
    protected void pushOutput(Level level, MachineBlockEntity machine, BlockPos pos) {
        if (machine.inventory().isEmpty()) {
            return;
        }
        InventoryModule inventory = machine.inventory().get();
        OutputItemHandler outputHandler = inventory.getOutputItemHandler(null);
        IOUtils.pushItemOutput(level, outputHandler, pos, outputHandler.getStartSlot(),
                outputHandler.getEndSlot(), 64);
    }
}
