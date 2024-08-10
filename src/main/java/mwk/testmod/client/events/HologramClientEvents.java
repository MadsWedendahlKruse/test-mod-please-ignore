package mwk.testmod.client.events;

import mwk.testmod.TestMod;
import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.client.render.hologram.events.ClearEvent;
import mwk.testmod.client.render.hologram.events.ProjectorEvent;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockUtils;
import mwk.testmod.common.item.tools.HologramProjectorItem;
import mwk.testmod.common.item.tools.WrenchItem;
import mwk.testmod.common.network.BuildMultiBlockPacket;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.TickEvent.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = TestMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
/**
 * Client-side events for rendering holograms and handling hologram projectors.
 */
public class HologramClientEvents {

    private static final double HOLOGRAM_PLACE_DISTANCE = 20.0;
    private static final double BLOCK_PLACE_DISTANCE = 5.0;
    private static final double BLOCK_BREAK_DISTANCE_SQR =
            BLOCK_PLACE_DISTANCE * BLOCK_PLACE_DISTANCE;
    private static final HologramRenderer HOLOGRAM_RENDERER = HologramRenderer.getInstance();

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        HOLOGRAM_RENDERER.onRenderLevelStage(event);
    }

    /**
     * Checks if a hologram needs to be updated when a block is placed or broken.
     *
     * @param pos the position of the block that was placed or broken
     */
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

    /**
     * Gets the block position that the player is looking at.
     *
     * @param player the player
     * @return the block position that the player is looking at, or null if the player is not
     * looking at a block
     */
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

    /**
     * Gets the hologram projector that the player is holding.
     *
     * @param player the player
     * @return the hologram projector that the player is holding, or an empty item stack if the
     * player is not holding a hologram projector
     */
    private static ItemStack getHologramProjector(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();
        return mainHandItem.getItem() instanceof HologramProjectorItem ? mainHandItem
                : offHandItem.getItem() instanceof HologramProjectorItem ? offHandItem
                        : ItemStack.EMPTY;
    }

    // The previous item that the player was holding
    private static ItemStack previousItem = ItemStack.EMPTY;
    // The previous blueprint key that was set by the hologram projector
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
            } else if (key != previousKey
                    || !(HOLOGRAM_RENDERER.getLatestEvent() instanceof ProjectorEvent)) {
                // If the key has changed, or if the blueprint was not previously set by the
                // but e.g. set by right-clicking a controller block, we want to override it
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
                // If the blueprint is null, and the blueprint was previously set by the projector,
                // we now want to clear the hologram
                HOLOGRAM_RENDERER.setEvent(new ClearEvent());
            }
        }
        previousItem = projector;
    }

    /**
     * Checks if a ray intersects an axis-aligned bounding box.
     *
     * @param pos  the starting position of the ray
     * @param dir  the direction of the ray
     * @param aabb the axis-aligned bounding box
     * @return true if the ray intersects the bounding box
     */
    private static boolean vecIntersectsAABB(Vec3 pos, Vec3 dir, AABB aabb) {
        // https://tavianator.com/2011/ray_box.html
        double tmin = Double.NEGATIVE_INFINITY, tmax = Double.POSITIVE_INFINITY;

        if (dir.x != 0.0) {
            double tx1 = (aabb.minX - pos.x) / dir.x;
            double tx2 = (aabb.maxX - pos.x) / dir.x;

            tmin = Math.max(tmin, Math.min(tx1, tx2));
            tmax = Math.min(tmax, Math.max(tx1, tx2));
        }

        if (dir.y != 0.0) {
            double ty1 = (aabb.minY - pos.y) / dir.y;
            double ty2 = (aabb.maxY - pos.y) / dir.y;

            tmin = Math.max(tmin, Math.min(ty1, ty2));
            tmax = Math.min(tmax, Math.max(ty1, ty2));
        }

        if (dir.z != 0.0) {
            double tz1 = (aabb.minZ - pos.z) / dir.z;
            double tz2 = (aabb.maxZ - pos.z) / dir.z;

            tmin = Math.max(tmin, Math.min(tz1, tz2));
            tmax = Math.min(tmax, Math.max(tz1, tz2));
        }

        return tmax >= tmin;

    }

    /**
     * Handles right click events for placing the controller block of a blueprint. For most
     * blueprints the controller block is placed above the ground, so this is used to allow the
     * controller to be placed even if there's no surrounding blocks to place it on.
     */
    private static void handleRightClick(PlayerInteractEvent event) {
        if (event.getItemStack() != ItemStack.EMPTY) {
            // Hologram Projector and Wrench right-clicks are handled elsewhere
            if (event.getItemStack().getItem() instanceof HologramProjectorItem
                    || event.getItemStack().getItem() instanceof WrenchItem) {
                return;
            }
        }
        // Prevent the event from triggering twice per right click
        if (event.getHand() == InteractionHand.OFF_HAND) {
            return;
        }
        Player player = event.getEntity();
        // Only do this for right clicks where the player isn't holding a hologram projector
        if (player == null || !getHologramProjector(player).isEmpty()) {
            return;
        }
        MultiBlockBlueprint blueprint = HOLOGRAM_RENDERER.getBlueprint();
        if (blueprint == null) {
            return;
        }
        BlockPos controllerPos = HOLOGRAM_RENDERER.getControllerPos();
        Direction facing = HOLOGRAM_RENDERER.getFacing();
        AABB aabb = blueprint.getAABB(controllerPos, facing);
        if (aabb == null) {
            return;
        }
        Vec3 eyePos = player.getEyePosition(1.0F);
        // Check if the player is too far away
        if (eyePos.distanceToSqr(controllerPos.getX(), controllerPos.getY(),
                controllerPos.getZ()) > BLOCK_BREAK_DISTANCE_SQR) {
            return;
        }
        Vec3 lookVec = player.getViewVector(1.0F);
        if (vecIntersectsAABB(eyePos, lookVec, aabb)) {
            Level level = player.level();
            // TODO: Using the previous key here is a bit of a hack
            PacketDistributor.SERVER.noArg()
                    .send(new BuildMultiBlockPacket(previousKey, controllerPos, facing));
            // Since we know the outcome of the packet (the controller block will be placed),
            // we can place the controller block on the client side as well, which allows us to
            // update the hologram immediately
            MultiBlockUtils.attemptBuildMultiBlock(level, blueprint, controllerPos, facing, player,
                    event.getHand(), true);
            player.swing(event.getHand());
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        handleRightClick(event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handleRightClick(event);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        handleRightClick(event);
    }

}
