package mwk.testmod.client.events;

import mwk.testmod.TestMod;
import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.client.render.hologram.events.ClearEvent;
import mwk.testmod.client.render.hologram.events.ProjectorEvent;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.item.HologramProjectorItem;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.TickEvent.ClientTickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod.EventBusSubscriber(modid = TestMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
/**
 * Client-side events for rendering holograms and handling hologram projectors.
 */
public class HologramClientEvents {

    private static final double HOLOGRAM_PLACE_DISTANCE = 20.0;
    private static final HologramRenderer HOLOGRAM_RENDERER = HologramRenderer.getInstance();

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        HOLOGRAM_RENDERER.onRenderLevelStage(event);
    }

    public static void checkHologramUpdate(BlockPos pos) {
        if (HOLOGRAM_RENDERER.isInsideHologram(pos)) {
            HOLOGRAM_RENDERER.updateBlueprintState();
        }
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        // TODO: This doesn't fire when the blueprint is automatically placed
        checkHologramUpdate(event.getPos());
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        checkHologramUpdate(event.getPos());
    }

    private static BlockPos getLookingAtBlockPos(Player player) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 reachVec = eyePos.add(lookVec.scale(HOLOGRAM_PLACE_DISTANCE));

        Level level = player.level();
        ClipContext clipContext = new ClipContext(eyePos, reachVec, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY, CollisionContext.empty());
        BlockHitResult hitResult = level.clip(clipContext);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return hitResult.getBlockPos();
        }
        return null;
    }

    private static ItemStack getHologramProjector(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();
        return mainHandItem.getItem() instanceof HologramProjectorItem ? mainHandItem
                : offHandItem.getItem() instanceof HologramProjectorItem ? offHandItem
                        : ItemStack.EMPTY;
    }

    private static ItemStack previousItem = ItemStack.EMPTY;
    private static ResourceKey<MultiBlockBlueprint> previousKey = null;
    private static MultiBlockBlueprint blueprint = null;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        Player player = minecraft.player;
        ItemStack projector = getHologramProjector(player);
        if (!projector.isEmpty()) {
            // If previousItem is empty the player has just equipped the projector
            if (previousItem.isEmpty()) {
                // If the latest hologram was not rendered by the projector, we don't want to
                // accidentally override it
                if (!(HOLOGRAM_RENDERER.getLatestEvent() instanceof ProjectorEvent)) {
                    HologramProjectorItem.setBlueprintKey(projector, null);
                }
            }
            ResourceKey<MultiBlockBlueprint> key = HologramProjectorItem.getBlueprintKey(projector);
            if (key == null) {
                blueprint = null;
            } else if (key != previousKey) {
                blueprint = minecraft.level.registryAccess()
                        .registry(TestModBlueprints.BLUEPRINT_REGISTRY_KEY)
                        .flatMap(blueprintRegistry -> blueprintRegistry.getOptional(key))
                        .orElse(null);
                previousKey = key;
            }
            if (blueprint != null) {
                BlockPos pos = getLookingAtBlockPos(player);
                if (pos != null) {
                    Direction facing = player.getDirection().getOpposite();
                    HOLOGRAM_RENDERER
                            .setEvent(new ProjectorEvent(minecraft.level, pos, blueprint, facing));
                }
            } else if (HOLOGRAM_RENDERER.getLatestEvent() instanceof ProjectorEvent) {
                HOLOGRAM_RENDERER.setEvent(new ClearEvent());
            }
        }
        previousItem = projector;
    }
}
