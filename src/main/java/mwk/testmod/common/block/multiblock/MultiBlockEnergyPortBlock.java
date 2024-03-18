package mwk.testmod.common.block.multiblock;

import mwk.testmod.common.block.multiblock.entity.MultiBlockEnergyPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockEnergyPortBlock extends MultiBlockPartBlock {

    public MultiBlockEnergyPortBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MultiBlockEnergyPortBlockEntity(pPos, pState);
    }
}
