package mwk.testmod.common.item.tools;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.interfaces.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A wrench for interacting with machines.
 */
public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos,
            Player player) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        TestMod.LOGGER.debug("WrenchItem::useOn");
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        if (state.getBlock() instanceof IWrenchable) {
            TestMod.LOGGER.debug("Wrenching " + state.getBlock() + " @ " + pos);
            IWrenchable block = (IWrenchable) level.getBlockState(pos).getBlock();
            if (block.onWrenched(state, level, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        }
        TestMod.LOGGER.debug("WrenchItem::useOn(super)");
        return super.useOn(context);
    }
}
