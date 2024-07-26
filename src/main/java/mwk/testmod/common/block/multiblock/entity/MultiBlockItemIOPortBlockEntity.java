package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public class MultiBlockItemIOPortBlockEntity extends MultiBlockIOPortBlockEntity {

    public MultiBlockItemIOPortBlockEntity(BlockPos pos, BlockState state, boolean input) {
        super(input ? TestModBlockEntities.MULTI_ITEM_INPUT_PORT_ENTITY_TYPE.get()
                : TestModBlockEntities.MULTI_ITEM_OUTPUT_PORT_ENTITY_TYPE.get(), pos, state, input,
                MachineBlockEntity::pullItemInput, MachineBlockEntity::ejectItemOutput);
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
}
