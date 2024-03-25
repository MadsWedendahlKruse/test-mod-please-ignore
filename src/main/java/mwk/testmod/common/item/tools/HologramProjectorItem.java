package mwk.testmod.common.item.tools;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.screen.HologramProjectorScreen;
import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.client.render.hologram.events.ProjectorEvent;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HologramProjectorItem extends Item {

    public static final String TAG_BLUEPRINT_LOCATION_NAMESPACE = "blueprint_location_namespace";
    public static final String TAG_BLUEPRINT_LOCATION_PATH = "blueprint_location_path";

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

    public static void setBlueprintKey(ItemStack projector,
            ResourceKey<MultiBlockBlueprint> blueprintKey) {
        if (blueprintKey == null) {
            TestMod.LOGGER.debug("Setting blueprint key to null");
            projector.getOrCreateTag().remove(TAG_BLUEPRINT_LOCATION_NAMESPACE);
            projector.getOrCreateTag().remove(TAG_BLUEPRINT_LOCATION_PATH);
            return;
        }
        TestMod.LOGGER.debug("Setting blueprint key to {}", blueprintKey);
        // Decompose the ResourceKey into its parts for eaiser reconstruction
        ResourceLocation location = blueprintKey.location();
        projector.getOrCreateTag().putString(TAG_BLUEPRINT_LOCATION_NAMESPACE,
                location.getNamespace());
        projector.getOrCreateTag().putString(TAG_BLUEPRINT_LOCATION_PATH, location.getPath());
    }

    public static ResourceKey<MultiBlockBlueprint> getBlueprintKey(ItemStack projector) {
        CompoundTag tag = projector.getOrCreateTag();
        if (!(tag.contains(TAG_BLUEPRINT_LOCATION_NAMESPACE)
                && tag.contains(TAG_BLUEPRINT_LOCATION_PATH))) {
            return null;
        }
        ResourceLocation location =
                new ResourceLocation(tag.getString(TAG_BLUEPRINT_LOCATION_NAMESPACE),
                        tag.getString(TAG_BLUEPRINT_LOCATION_PATH));
        return ResourceKey.create(TestModBlueprints.BLUEPRINT_REGISTRY_KEY, location);
    }
}
