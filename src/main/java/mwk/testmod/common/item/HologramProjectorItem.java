package mwk.testmod.common.item;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.HologramProjectorScreen;
import mwk.testmod.client.hologram.HologramRenderer;
import mwk.testmod.client.hologram.events.ProjectorEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HologramProjectorItem extends Item {

    public HologramProjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player,
            InteractionHand hand) {
        if (level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                Minecraft.getInstance().setScreen(new HologramProjectorScreen());
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            }
            HologramRenderer renderer = HologramRenderer.getInstance();
            if (renderer.getLatestEvent() instanceof ProjectorEvent) {
                boolean locked = renderer.isLocked();
                renderer.setLocked(!locked);
                if (locked) {
                    player.displayClientMessage(Component.translatable(
                            "info.testmod.hologram_projector.blueprint.unlocked"), true);
                } else {
                    player.displayClientMessage(Component.translatable(
                            "info.testmod.hologram_projector.blueprint.locked"), true);
                }
                return InteractionResultHolder.success(player.getItemInHand(hand));
            } else {
                player.displayClientMessage(
                        Component.translatable("info.testmod.hologram_projector.blueprint.help"),
                        true);
            }
        }
        return super.use(level, player, hand);
    }

    public static void setBlueprintKey(ItemStack projector, String blueprintName) {
        TestMod.LOGGER.debug("Setting blueprint key to {}", blueprintName);
        projector.getOrCreateTag().putString("blueprint", blueprintName);
    }

    public static String getBlueprintKey(ItemStack projector) {
        CompoundTag tag = projector.getOrCreateTag();
        return !tag.contains("blueprint") ? "" : tag.getString("blueprint");
    }
}
