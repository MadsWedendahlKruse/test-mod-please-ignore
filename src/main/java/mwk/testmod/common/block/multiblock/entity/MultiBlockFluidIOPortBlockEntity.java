package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MultiBlockFluidIOPortBlockEntity extends MultiBlockIOPortBlockEntity {

    // TODO: Conduits don't connect to this port for some reason
    public MultiBlockFluidIOPortBlockEntity(BlockPos pos, BlockState state, boolean input) {
        super(input ? TestModBlockEntities.MULTI_FLUID_INPUT_PORT_ENTITY_TYPE.get()
                : TestModBlockEntities.MULTI_FLUID_OUTPUT_PORT_ENTITY_TYPE.get(), pos, state, input,
                MachineBlockEntity::pullFluidInput, MachineBlockEntity::ejectFluidOutput);
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
}
