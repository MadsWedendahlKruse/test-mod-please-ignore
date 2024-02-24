package mwk.testmod.client.hologram;

import org.joml.Quaternionf;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintState;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.block.multiblock.controller.MultiBlockControllerBlock;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HologramRenderer {

    private MultiBlockBlueprint blueprint;
    private BlueprintState blueprintState;
    private BlockPos controllerPos;
    private Direction facing;
    private Level level;

    private HologramBlockRenderer hologramBlockRenderer;
    private HologramItemRenderer hologramItemRenderer;
    private HologramTextRenderer hologramTextRenderer;

    private static HologramRenderer instance;

    private HologramRenderer() {}

    public static synchronized HologramRenderer getInstance() {
        if (instance == null) {
            instance = new HologramRenderer();
        }
        return instance;
    }

    public void setup() {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        hologramBlockRenderer = new HologramBlockRenderer(blockRenderer, bufferSource);
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        hologramItemRenderer = new HologramItemRenderer(itemRenderer, bufferSource);
        Font font = minecraft.font;
        hologramTextRenderer = new HologramTextRenderer(font, bufferSource);
    }

    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (controllerPos == null) {
            return;
        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            renderBlueprintHologram(Minecraft.getInstance(), event, level, controllerPos, blueprint,
                    facing);
        }
    }

    /**
     * Push the pose of the top left corner on a given face of the block at the given position. This
     * assumes the last pose is at the bottom north west corner of the controller.
     * 
     * @param poseStack the pose stack
     * @param pos the position of the block relative to the controller. If null the pose is not
     *        translated
     * @param facing the face to get the pose for
     */
    private void pushTopLeftPose(PoseStack poseStack, BlockPos pos, Direction face) {
        poseStack.pushPose();
        if (pos != null) {
            // Move to bottom north west corner of the block
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        }
        // Move to top north east corner of the block
        poseStack.translate(1.0F, 1.0F, 0.0F);
        // Rotate to the correct face
        double angle = 0.0;
        switch (facing) {
            case SOUTH:
                angle = Math.PI;
                break;
            case EAST:
                angle = -Math.PI / 2.0;
                break;
            case WEST:
                angle = Math.PI / 2.0;
                break;
            case NORTH:
            default:
                break;
        }
        Quaternionf q = new Quaternionf(0.0F, (float) Math.sin(angle / 2.0F), 0.0F,
                (float) Math.cos(angle / 2.0F));
        // Rotate around the center of the block
        poseStack.rotateAround(q, -0.5F, -0.5F, 0.5F);
    }

    private void renderBlueprintHologram(Minecraft minecraft, RenderLevelStageEvent event,
            Level level, BlockPos controllerPos, MultiBlockBlueprint blueprint, Direction facing) {
        hologramBlockRenderer.updateAnimation();

        PoseStack poseStack = event.getPoseStack();
        // Push once to save the origin
        poseStack.pushPose();
        // Translate to the render position
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        poseStack.translate(-camPos.x + controllerPos.getX(), -camPos.y + controllerPos.getY(),
                -camPos.z + controllerPos.getZ());

        // Set the render state
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // Render the overlay on the incorrect blocks
        hologramBlockRenderer.renderHologramBlockOverlays(poseStack,
                blueprintState.getIncorrectBlocks(), facing, HologramConfig.RED, true, false);
        // Render the hologram of the empty blocks
        hologramBlockRenderer.renderHologramBlocks(poseStack, blueprintState.getEmptyBlocks(),
                facing, HologramConfig.WHITE, true, true);
        // Render the overlay on the empty blocks
        hologramBlockRenderer.renderHologramBlockOverlays(poseStack,
                blueprintState.getEmptyBlocks(), facing, HologramConfig.CYAN, true, true);
        // Render the overlay on the controller so the player can see which one it is
        // TODO: Different way to highlight the controller. Right now it looks like it's also a
        // hologram, which it isn't necessarily
        float[] controllerColor =
                blueprintState.isComplete() ? HologramConfig.GREEN : HologramConfig.YELLOW;
        hologramBlockRenderer.renderHologramBlock(poseStack, new BlockPos(0, 0, 0), null, facing,
                controllerColor, true, false);
        // Reset shader color after rendering the hologram
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pushTopLeftPose(poseStack, null, facing);
        hologramItemRenderer.renderBlueprintStatus(poseStack, hologramTextRenderer, blueprintState);
        // Pop the top left pose pushed during pushTopLeftPose
        poseStack.popPose();

        // Reset the render state
        RenderSystem.disableBlend();
        // Pop the origin
        poseStack.popPose();
        // Reset shader color
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Set the controller for which to render the hologram.
     * 
     * @param level the level the controller is in
     * @param pos the position of the controller
     */
    public void setController(Level level, BlockPos pos) {
        // Only modify the renderer on the client side
        if (!level.isClientSide()) {
            return;
        }
        TestMod.LOGGER.debug("HologramRenderer::setController");
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof MultiBlockControllerBlock controller) {
            TestMod.LOGGER.debug("Setting controller: " + controller + " @ " + pos);
            this.level = level;
            controllerPos = pos;
            blueprint = controller.getBlueprint();
            blueprintState = blueprint.getState(level, pos);
            facing = state.getValue(MultiBlockControllerBlock.FACING);
            hologramBlockRenderer.startAnimation();
            updateBlueprintState();
        }
    }

    /**
     * Check if the given position is the current controller for which to render the hologram.
     * 
     * @param pos the position to check
     */
    public boolean isCurrentController(BlockPos pos) {
        TestMod.LOGGER.debug("HologramRenderer::isCurrentController");
        TestMod.LOGGER.debug("pos: " + pos);
        TestMod.LOGGER.debug("controllerPos: " + controllerPos);
        return controllerPos != null && controllerPos.equals(pos);
    }

    /**
     * Clear the controller for which to render the hologram. This is used when the multiblock
     * structure is formed or when the controller is destroyed.
     */
    public void clearController() {
        TestMod.LOGGER.debug("HologramRenderer::clearController");
        blueprint = null;
        blueprintState = null;
        level = null;
        controllerPos = null;
        facing = null;
    }

    /**
     * Clear the controller for which to render the hologram if the given position is the current
     * controller.
     * 
     * @param pos the position to check
     */
    public void clearIfCurrentController(BlockPos pos) {
        if (isCurrentController(pos)) {
            clearController();
        }
    }

    /**
     * Toggle the controller for which to render the hologram. If the given position is the current
     * controller, clear the controller. Otherwise, set the controller.
     * 
     * @param level the level the controller is in
     * @param pos the position of the controller
     */
    public void toggleController(Level level, BlockPos pos) {
        // Only modify the renderer on the client side
        if (!level.isClientSide()) {
            return;
        }
        if (isCurrentController(pos)) {
            clearController();
        } else {
            setController(level, pos);
        }
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
}
