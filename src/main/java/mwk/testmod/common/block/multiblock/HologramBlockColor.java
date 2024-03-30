package mwk.testmod.common.block.multiblock;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class HologramBlockColor implements BlockColor {

    @Override
    public int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (state.getBlock() instanceof HologramBlock) {
            switch (state.getValue(HologramBlock.COLOR)) {
                case RED:
                    return 0xFF0000;
                case GREEN:
                    return 0x0000FF;
                case CYAN:
                default:
                    return 0x00FFFF;
            }
        }
        return 0xFFFFFF;
    }
}
