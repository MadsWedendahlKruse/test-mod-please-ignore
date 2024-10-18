package mwk.testmod.common.block.entity;

import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblerBlockEntity extends BlockEntity {

    public AssemblerBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestModBlockEntities.ASSEMBLER_ENTITY_TYPE.get(), pos, blockState);
    }
}
