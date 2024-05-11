package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.base.BaseMachineBlockEntity;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public class MutliBlockIOPortBlockEntity extends MultiBlockPartBlockEntity implements ITickable {

    private final boolean input;

    public MutliBlockIOPortBlockEntity(BlockPos pos, BlockState state, boolean input) {
        super(input ? TestModBlockEntities.MULTI_INPUT_PORT_ENTITY_TYPE.get()
                : TestModBlockEntities.MULTI_OUTPUT_PORT_ENTITY_TYPE.get(), pos, state);
        this.input = input;
    }

    @Override
    public IItemHandler getItemHandler(Direction direction) {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof BaseMachineBlockEntity machine) {
                return input ? machine.getInputHandler(direction, false)
                        : machine.getOutputHandler(direction);
            }
        }
        return null;
    }

    @Override
    public void tick() {
        if (!isFormed()) {
            return;
        }
        BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
        if (controllerEntity instanceof BaseMachineBlockEntity machine) {
            if (input) {
                machine.pullInput(this.worldPosition);
            } else {
                machine.ejectOutput(this.worldPosition);
            }
        }
    }
}
