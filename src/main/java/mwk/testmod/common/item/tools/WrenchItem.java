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
import net.minecraft.world.phys.Vec3;

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
        InteractionHand hand = context.getHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        Vec3 clickLocation = context.getClickLocation();
        if (state.getBlock() instanceof IWrenchable wrenchable) {
            TestMod.LOGGER.debug("Wrenching " + state.getBlock() + " @ " + pos);
            if (wrenchable.onWrenched(state, level, pos, player, hand, clickLocation)) {
                return InteractionResult.SUCCESS;
            }
        }
        TestMod.LOGGER.debug("WrenchItem::useOn(super)");
        return super.useOn(context);
    }
}
