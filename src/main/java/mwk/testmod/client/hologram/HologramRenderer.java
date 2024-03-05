package mwk.testmod.client.hologram;

import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.client.animations.dynamic.DynamicAnimationAngle;
import mwk.testmod.client.animations.dynamic.DynamicAnimationVector;
import mwk.testmod.client.hologram.components.HologramBlockRenderer;
import mwk.testmod.client.hologram.components.HologramGeometryRenderer;
import mwk.testmod.client.hologram.components.HologramItemRenderer;
import mwk.testmod.client.hologram.components.HologramTextRenderer;
import mwk.testmod.client.hologram.events.HologramEvent;
import mwk.testmod.common.block.multiblock.HologramBlock.HologramColor;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintState;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HologramRenderer {

    private MultiBlockBlueprint blueprint;
    private BlueprintState blueprintState;
    private BlockPos controllerPos;
    private Direction facing;
    boolean locked = false;

    private HologramBlockRenderer hologramBlockRenderer;
    private HologramItemRenderer hologramItemRenderer;
    private HologramTextRenderer hologramTextRenderer;
    private HologramGeometryRenderer hologramGeometryRenderer;

    private HologramEvent latestEvent;

    private DynamicAnimationVector moveAnimation;
    private static final float HOLOGRAM_MOVE_SPEED = 10.0F; // [m/s]

    private DynamicAnimationAngle rotateAnimation;
    private static final float HOLOGRAM_ROTATE_SPEED = (float) Math.PI * 4; // [rad/s]

    private static HologramRenderer instance;

    private HologramRenderer() {}

    public static synchronized HologramRenderer getInstance() {
        if (instance == null) {
            instance = new HologramRenderer();
        }
        return instance;
    }

    public void init() {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        hologramBlockRenderer = new HologramBlockRenderer(blockRenderer, bufferSource);
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        hologramItemRenderer = new HologramItemRenderer(itemRenderer, bufferSource);
        Font font = minecraft.font;
        hologramTextRenderer = new HologramTextRenderer(font, bufferSource);
        hologramGeometryRenderer = new HologramGeometryRenderer(bufferSource);

        moveAnimation = new DynamicAnimationVector(HOLOGRAM_MOVE_SPEED);
        rotateAnimation = new DynamicAnimationAngle(HOLOGRAM_ROTATE_SPEED);
    }

    public void setEvent(HologramEvent event) {
        event.apply(this);
        latestEvent = event;
    }

    public HologramEvent getLatestEvent() {
        return latestEvent;
    }

    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (controllerPos == null) {
            return;
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            renderBlueprintHologram(Minecraft.getInstance(), event, controllerPos, blueprint,
                    facing);
        }
    }

    private void renderBlueprintHologram(Minecraft minecraft, RenderLevelStageEvent event,
            BlockPos controllerPos, MultiBlockBlueprint blueprint, Direction facing) {
        hologramBlockRenderer.updateAnimation();
        moveAnimation.update();
        rotateAnimation.update();

        PoseStack poseStack = event.getPoseStack();
        // Push once to save the origin
        poseStack.pushPose();
        // Translate to the render position
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        Vec3 renderPos = moveAnimation.getValue();
        poseStack.translate(renderPos.x - camPos.x, renderPos.y - camPos.y, renderPos.z - camPos.z);

        // Rotate the hologram to the correct facing
        float rotation = rotateAnimation.getValue();
        // Rotate around the center of the block
        poseStack.rotateAround(new Quaternionf().rotationY(rotation), 0.5F, 0.5F, 0.5F);

        // Render the overlay on the incorrect blocks
        hologramBlockRenderer.renderHologramBlockOverlays(poseStack,
                blueprintState.getIncorrectBlocks(), HologramColor.RED, true);
        // Render the hologram of the empty blocks
        hologramBlockRenderer.renderHologramBlocks(poseStack, blueprintState.getEmptyBlocks(),
                true);
        // Render the overlay on the empty blocks
        hologramBlockRenderer.renderHologramBlockOverlays(poseStack,
                blueprintState.getEmptyBlocks(), HologramColor.CYAN, true);

        // Render the outline on the controller so the player can see which one it is
        HologramColor outlineColor = blueprintState.isComplete() ? HologramColor.GREEN
                : blueprintState.getIncorrectBlocks().isEmpty() ? HologramColor.CYAN
                        : HologramColor.RED;
        AABB aabb = blueprint.getAABB();
        hologramGeometryRenderer.drawAABB(poseStack, aabb, outlineColor.getFloatColor(), 1.0F);
        hologramGeometryRenderer.drawBlockOutline(poseStack, outlineColor.getFloatColor(), 1.0F);
        // TODO: Unfinished attempt to draw the hologram with a projection
        // Vec3 lookDir = minecraft.player.getLookAngle();
        // Vec3 camPosRelative =
        // camPos.subtract(controllerPos.getX(), controllerPos.getY(), controllerPos.getZ());
        // Vector3f upDirF = camera.getUpVector();
        // Vec3 upDir = new Vec3(upDirF.x(), upDirF.y(), upDirF.z());
        // hologramGeometryRenderer.drawAABBWithProjection(poseStack, aabb, outlineColor, 1.0F,
        // camPosRelative, lookDir, upDir);

        // Move to top left corner of the controller block
        poseStack.translate(1.0F, 1.0F, 0.0F);
        hologramItemRenderer.renderBlueprintStatus(poseStack, hologramTextRenderer, blueprintState);

        poseStack.popPose();
    }

    private float getRotation(Direction facing) {
        switch (facing) {
            case EAST:
                return -(float) Math.PI / 2.0F;
            case WEST:
                return (float) Math.PI / 2.0F;
            case SOUTH:
                return (float) Math.PI;
            case NORTH:
            default:
                return 0.0F;
        }
    }

    public boolean isCurrentBlueprint(BlockPos controllerPos, MultiBlockBlueprint blueprint,
            Direction facing) {
        return this.controllerPos != null && this.controllerPos.equals(controllerPos)
                && this.blueprint.equals(blueprint) && this.facing.equals(facing);
    }

    /**
     * Set the hologram to render the given blueprint.
     * 
     * @param controllerPos the position of the controller
     * @param blueprint the blueprint to render
     * @param blueprintState the state of the blueprint
     * @param facing the direction the blueprint should be rendered in
     * @param animatePopIn whether to animate the hologram popping in
     * @param animateMove whether to animate the hologram moving to the new position
     */
    public void setHologramBlueprint(BlockPos controllerPos, MultiBlockBlueprint blueprint,
            BlueprintState blueprintState, Direction facing, boolean animatePopIn,
            boolean animateMove) {
        if (isCurrentBlueprint(controllerPos, blueprint, facing)) {
            return;
        }
        this.controllerPos = controllerPos;
        this.blueprint = blueprint;
        this.blueprintState = blueprintState;
        this.facing = facing;
        setLocked(false);
        updateBlueprintState();
        if (controllerPos == null || facing == null) {
            moveAnimation.reset();
            rotateAnimation.reset();
            return;
        }
        if (animatePopIn) {
            hologramBlockRenderer.startAnimation();
        }
        Vec3 targetPos = new Vec3(controllerPos.getX(), controllerPos.getY(), controllerPos.getZ());
        moveAnimation.setTargetValue(targetPos);
        float targetRotation = getRotation(facing);
        rotateAnimation.setTargetValue(targetRotation);
        if (animateMove) {
            moveAnimation.start();
            if (!moveAnimation.startValueSet()) {
                moveAnimation.setStartValue(targetPos);
            }
            rotateAnimation.start();
            if (!rotateAnimation.startValueSet()) {
                rotateAnimation.setStartValue(targetRotation);
            }
        } else {
            moveAnimation.stop();
            rotateAnimation.stop();
        }
    }

    /**
     * Clear the hologram so it doesn't render anything.
     */
    public void clearHologram() {
        setHologramBlueprint(null, null, null, null, false, false);
    }

    /**
     * Check if the given position is inside the hologram of the current controller.
     * 
     * @param pos the position to check
     */
    public boolean isInsideHologram(BlockPos pos) {
        if (controllerPos == null) {
            return false;
        }
        AABB aabb = blueprint.getAABB(controllerPos, facing);
        return aabb.contains(pos.getX(), pos.getY(), pos.getZ());
    }

    public void updateBlueprintState() {
        if (blueprintState == null) {
            return;
        }
        blueprintState.update();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }
}
