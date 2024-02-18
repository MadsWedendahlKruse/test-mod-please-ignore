package mwk.testmod.common.item;

import java.util.List;

import mwk.testmod.TestMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        TestMod.LOGGER.info("WrenchItem::useOn");
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        if (state.getBlock() instanceof Wrenchable) {
            TestMod.LOGGER.info("Wrenching " + state.getBlock() + " @ " + pos);
            Wrenchable block = (Wrenchable) level.getBlockState(pos).getBlock();
            if (block.onWrenched(state, level, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        }
        TestMod.LOGGER.info("WrenchItem::useOn(super)");
        return super.useOn(context);
    }
}
