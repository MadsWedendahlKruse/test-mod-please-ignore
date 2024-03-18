package mwk.testmod.common.block.multiblock;

import mwk.testmod.common.block.multiblock.entity.MutliBlockIOPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MutliBlockIOPortBlock extends MultiBlockPartBlock {

    private final boolean input;

    public MutliBlockIOPortBlock(Properties properties, boolean input) {
        super(properties);
        this.input = input;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MutliBlockIOPortBlockEntity(pos, state, input);
    }
}
