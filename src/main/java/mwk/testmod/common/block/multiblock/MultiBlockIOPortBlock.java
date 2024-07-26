package mwk.testmod.common.block.multiblock;

import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.multiblock.entity.MultiBlockIOPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockIOPortBlock extends MultiBlockPartBlock {

    private final boolean input;
    private final MultiBlockPortBlockEntityFactory blockEntityFactory;

    public MultiBlockIOPortBlock(Properties properties, boolean input,
            MultiBlockPortBlockEntityFactory blockEntityFactory) {
        super(properties);
        this.input = input;
        this.blockEntityFactory = blockEntityFactory;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFactory.create(pos, state, input);
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

    @FunctionalInterface
    public interface MultiBlockPortBlockEntityFactory {
        MultiBlockIOPortBlockEntity create(BlockPos pos, BlockState state, boolean input);
    }
}
