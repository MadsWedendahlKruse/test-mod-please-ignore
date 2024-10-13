package mwk.testmod.common.item.tools;

import mwk.testmod.client.gui.screen.HologramProjectorScreen;
import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.client.render.hologram.events.ProjectorEvent;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HologramProjectorItem extends Item {

    // TODO: It's not the cleanest solution to store the blueprint key in the item class, but it gets the job done
    private static ResourceKey<MultiBlockBlueprint> blueprintKey;

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
                    player.displayClientMessage(
                            Component.translatable(
                                    TestModLanguageProvider.KEY_INFO_HOLOGRAM_PROJECTOR_UNLOCKED),
                            true);
                } else {
                    player.displayClientMessage(
                            Component.translatable(
                                    TestModLanguageProvider.KEY_INFO_HOLOGRAM_PROJECTOR_LOCKED),
                            true);
                }
                return InteractionResultHolder.success(player.getItemInHand(hand));
            } else {
                player.displayClientMessage(Component.translatable(
                        TestModLanguageProvider.KEY_INFO_HOLOGRAM_PROJECTOR_HELP), true);
            }
        }
        return super.use(level, player, hand);
    }

    public static void setBlueprintKey(ResourceKey<MultiBlockBlueprint> key) {
        blueprintKey = key;
    }

    public static ResourceKey<MultiBlockBlueprint> getBlueprintKey() {
        return blueprintKey;
    }
}
