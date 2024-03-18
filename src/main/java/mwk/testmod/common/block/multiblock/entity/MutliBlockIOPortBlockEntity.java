package mwk.testmod.common.block.multiblock.entity;

import mwk.testmod.common.block.entity.base.BaseMachineBlockEntity;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public class MutliBlockIOPortBlockEntity extends MultiBlockPartBlockEntity {

    private final boolean input;

    public MutliBlockIOPortBlockEntity(BlockPos pos, BlockState state, boolean input) {
        super(input ? TestModBlockEntities.MULTI_BLOCK_INPUT_PORT_BLOCK_ENTITY_TYPE.get()
                : TestModBlockEntities.MULTI_BLOCK_OUTPUT_PORT_BLOCK_ENTITY_TYPE.get(), pos, state);
        this.input = input;
    }

    @Override
    public IItemHandler getItemHandler(Direction direction) {
        if (isFormed()) {
            BlockEntity controllerEntity = level.getBlockEntity(controllerPos);
            if (controllerEntity instanceof BaseMachineBlockEntity itemEntity) {
                return input ? itemEntity.getInputHandler(direction)
                        : itemEntity.getOutputHandler(direction);
            }
        }
        return null;
    }
}
