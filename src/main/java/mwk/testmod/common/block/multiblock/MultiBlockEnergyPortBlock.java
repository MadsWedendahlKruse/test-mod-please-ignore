package mwk.testmod.common.block.multiblock;

import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.multiblock.entity.MultiBlockEnergyPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockEnergyPortBlock extends MultiBlockPartBlock {

    public MultiBlockEnergyPortBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MultiBlockEnergyPortBlockEntity(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        // Lambda expression that implements the BlockEntityTicker interface.
        return (lvl, pos, st, be) -> {
            if (be instanceof ITickable tickable) {
                tickable.tick();
            }
        };
    }
}
