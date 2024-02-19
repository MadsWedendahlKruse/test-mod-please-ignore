package mwk.testmod.client;

import java.util.ArrayList;
import javax.annotation.Nullable;
import org.joml.Quaternionf;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint.BlueprintCheckResult;
import mwk.testmod.common.block.multiblock.controller.MultiBlockControllerBlock;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = TestMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
public class HologramRenderer {

    private static final float HOLOGRAM_OVERLAY_ALPHA = 0.5F;
    private static final float HOLOGRAM_BLOCK_ALPHA = 1.0F;
    private static final float HOLOGRAM_ITEM_ALPHA = 1.0F;
    private static final float HOLOGRAM_ITEM_SCALE = 0.15F;
    private static final float HOLOGRAM_ITEM_SPACING = 0.05F;
    private static final float HOLOGRAM_TEXT_SCALE = 0.01F;
    private static final float HOLOGRAM_TEXT_Z_OFFSET = -0.1F;
    // Text is for some reason rendered upside down by default
    private static final Quaternionf HOLOGRAM_TEXT_ROTATION = new Quaternionf(0.0F, 0.0F,
            (float) Math.sin(Math.PI / 2.0F), (float) Math.cos(Math.PI / 2.0F));
    private static final Quaternionf WRENCH_ITEM_ROTATION = new Quaternionf(0.0F,
            (float) Math.sin(Math.PI / 2.0F), 0.0F, (float) Math.cos(Math.PI / 2.0F));
    private static final float HOLOGRAM_ANIMATION_DURATION = 0.15F;

    private static final float[] WHITE = new float[] {1.0F, 1.0F, 1.0F};
    private static final float[] CYAN = new float[] {0.2F, 1.0F, 1.0F};
    private static final float[] RED = new float[] {1.0F, 0.0F, 0.0F};
    private static final float[] GREEN = new float[] {0.0F, 1.0F, 0.0F};
    private static final float[] YELLOW = new float[] {1.0F, 1.0F, 0.0F};
    private static final int TEXT_WHITE = 16777215;

    // TOOD: What does this number actually mean?
    private static final int PACKED_LIGHT_COORDS = 15728880;

    private static BlockPos controllerPos;
    private static MultiBlockBlueprint blueprint;
    private static Direction facing;
    private static Level level;
    // --- Animation ---
    private static long lastTime;
    private static float elapsedTime;
    private static float scale;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (controllerPos == null) {
            return;
        }
        // Render a hologram of the multiblock
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            renderBlueprintHologram(Minecraft.getInstance(), event, level, controllerPos, blueprint,
                    facing);
        }
    }

    /**
     * Renders a hologram at the given position.
     * 
     * @param poseStack the pose stack
     * @param blockRenderer the block renderer
     * @param bufferSource the buffer source
     * @param position the position of the hologram
     * @param state the block state of the hologram. If null the hologram overlay is rendered
     * @param color the color of the hologram. If null the current shader color is used
     */
    private static void renderHologramBlock(PoseStack poseStack,
            BlockRenderDispatcher blockRenderer, BufferSource bufferSource, BlockPos position,
            @Nullable BlockState state, @Nullable float[] color, boolean endBatch,
            boolean animate) {
        if (color != null) {
            RenderSystem.setShaderColor(color[0], color[1], color[2], HOLOGRAM_OVERLAY_ALPHA);
        }
        BlockState blockState;
        if (state != null) {
            // Make sure the controller is facing the right direction
            if (state.getBlock() instanceof MultiBlockControllerBlock) {
                state = state.setValue(MultiBlockControllerBlock.FACING, facing);
            }
            blockState = state;
        } else {
            blockState = TestMod.HOLOGRAM_BLOCK.get().defaultBlockState();
        }
        poseStack.pushPose();
        poseStack.translate(position.getX(), position.getY(), position.getZ());
        if (animate) {
            // Animate the scale of the hologram when it is first rendered
            // Make it grow from the center
            poseStack.translate(0.5F - scale / 2.0F, 0.5F - scale / 2.0F, 0.5F - scale / 2.0F);
            poseStack.scale(scale, scale, scale);
        }
        blockRenderer.renderSingleBlock(blockState, poseStack, bufferSource, PACKED_LIGHT_COORDS,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        if (endBatch) {
            bufferSource.endBatch();
        }
    }

    /**
     * Renders holograms of the blocks at the given positions.
     * 
     * @param poseStack the pose stack
     * @param blockRenderer the block renderer
     * @param bufferSource the buffer source
     * @param positions the positions of the blocks
     * @param states the block states of the blocks
     * @param indices the indices of the blocks to render
     * @param color the color of the hologram
     */
    private static void renderHologramBlocks(PoseStack poseStack,
            BlockRenderDispatcher blockRenderer, BufferSource bufferSource, BlockPos[] positions,
            BlockState[] states, ArrayList<Integer> indices, float[] color, boolean animate) {
        // Using the overlay alpha by default is a bit a sneaky trick, since when rendering full
        // blocks the alpha is not applied
        RenderSystem.setShaderColor(color[0], color[1], color[2], HOLOGRAM_OVERLAY_ALPHA);
        for (Integer i : indices) {
            BlockState state = states != null ? states[i] : null;
            renderHologramBlock(poseStack, blockRenderer, bufferSource, positions[i], state, null,
                    false, animate);
        }
        bufferSource.endBatch();
    }

    /**
     * Push the pose of the top left corner of the block at the given position. This assumes the
     * last pose is at the bottom north west corner of the controller.
     * 
     * @param poseStack the pose stack
     * @param pos the position of the block relative to the controller. If null the pose is not
     *        translated
     * @param facing the face to get the pose for
     */
    private static void pushTopLeftPose(PoseStack poseStack, BlockPos pos, Direction face) {
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

    private static void renderText(PoseStack poseStack, Font font, BufferSource bufferSource,
            Component component, float x, float y, float z, int textColor) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(HOLOGRAM_TEXT_SCALE, HOLOGRAM_TEXT_SCALE, HOLOGRAM_TEXT_SCALE);
        poseStack.rotateAround(HOLOGRAM_TEXT_ROTATION, 0.0F, 0.0F, 0.0F);
        font.drawInBatch(component, 0, 0, textColor, false, poseStack.last().pose(), bufferSource,
                DisplayMode.SEE_THROUGH, 0, PACKED_LIGHT_COORDS);
        poseStack.popPose();
    }

    private static void renderItemStack(PoseStack poseStack, ItemRenderer itemRenderer,
            BufferSource bufferSource, ItemStack itemStack, float x, float y, float z,
            float[] color, boolean endBatch) {
        if (color != null) {
            RenderSystem.setShaderColor(color[0], color[1], color[2], HOLOGRAM_ITEM_ALPHA);
        }
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(HOLOGRAM_ITEM_SCALE, HOLOGRAM_ITEM_SCALE, HOLOGRAM_ITEM_SCALE);
        itemRenderer.renderStatic(null, itemStack, ItemDisplayContext.NONE, false, poseStack,
                bufferSource, null, PACKED_LIGHT_COORDS, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
        if (endBatch) {
            bufferSource.endBatch();
        }
    }

    /**
     * Renders a list of itemstacks at the given position. The title is rendered at the top of the
     * list. The itemstacks are rendered below the title. The height of the rendered list is
     * returned to make it easier to render multiple lists on top of each other.
     */
    private static float renderItemStackList(PoseStack poseStack, ItemRenderer itemRenderer,
            Font font, BufferSource bufferSource, ArrayList<ItemStack> itemStacks, Component title,
            float x, float y, float z, float[] color, boolean endBatch) {
        float height = 0;
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        // Render the title if it exists
        if (title != null) {
            renderText(poseStack, font, bufferSource, title, 0, 0, 0, TEXT_WHITE);
            height += HOLOGRAM_ITEM_SCALE + HOLOGRAM_ITEM_SPACING;
        }
        // Render the itemstacks
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack itemStack = itemStacks.get(i);
            // Render the itemstack itself
            renderItemStack(poseStack, itemRenderer, bufferSource, itemStack,
                    -HOLOGRAM_ITEM_SCALE / 2,
                    -(height + i * (HOLOGRAM_ITEM_SCALE + HOLOGRAM_ITEM_SPACING)),
                    -HOLOGRAM_ITEM_SCALE, color, false);
            // Render the count of the itemstack
            String itemCountString = itemStack.getCount() + " ";
            Component component =
                    Component.literal(itemCountString).append(itemStack.getHoverName());
            renderText(poseStack, font, bufferSource, component,
                    -(HOLOGRAM_ITEM_SCALE + HOLOGRAM_ITEM_SPACING),
                    -(height + i * (HOLOGRAM_ITEM_SCALE + HOLOGRAM_ITEM_SPACING))
                            + HOLOGRAM_ITEM_SCALE / 4,
                    HOLOGRAM_TEXT_Z_OFFSET, TEXT_WHITE);
        }
        height += itemStacks.size() * (HOLOGRAM_ITEM_SCALE + HOLOGRAM_ITEM_SPACING);
        poseStack.popPose();
        if (endBatch) {
            bufferSource.endBatch();
        }
        return height;
    }

    private static void renderBlueprintShoppingList(PoseStack poseStack, ItemRenderer itemRenderer,
            Font font, BufferSource bufferSource, ArrayList<ItemStack> missingBlockStacks,
            ArrayList<ItemStack> wrongBlockStacks) {
        float height = 0;
        if (!missingBlockStacks.isEmpty()) {
            height -= renderItemStackList(poseStack, itemRenderer, font, bufferSource,
                    missingBlockStacks,
                    Component.translatable(
                            "info.testmod.controller.blueprint.hologram.missing_blocks"),
                    0.0F, height, 0.0F, null, false);
        }
        if (!wrongBlockStacks.isEmpty()) {
            height -= renderItemStackList(poseStack, itemRenderer, font, bufferSource,
                    wrongBlockStacks,
                    Component.translatable(
                            "info.testmod.controller.blueprint.hologram.incorrect_blocks"),
                    0.0F, height, 0.0F, null, false);
        }
        bufferSource.endBatch();
    }

    private static void renderBlueprintComplete(PoseStack poseStack, ItemRenderer itemRenderer,
            Font font, BufferSource bufferSource) {
        renderText(poseStack, font, bufferSource,
                Component.translatable("info.testmod.controller.blueprint.hologram.complete"), 0.0F,
                0.0F, HOLOGRAM_TEXT_Z_OFFSET, TEXT_WHITE);
        renderText(poseStack, font, bufferSource,
                Component.translatable("info.testmod.controller.blueprint.hologram.right_click_me"),
                0.0F, -(HOLOGRAM_ITEM_SCALE + HOLOGRAM_ITEM_SPACING), HOLOGRAM_TEXT_Z_OFFSET,
                TEXT_WHITE);
        bufferSource.endBatch();
        // Render a wrench at the center of the controller
        poseStack.pushPose();
        poseStack.translate(-0.5F, -0.65F, -0.25F);
        poseStack.rotateAround(WRENCH_ITEM_ROTATION, 0.0F, 0.0F, 0.0F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        ItemStack wrenchStack = new ItemStack(TestMod.WRENCH_ITEM.get());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, HOLOGRAM_OVERLAY_ALPHA);
        itemRenderer.renderStatic(null, wrenchStack, ItemDisplayContext.GUI, false, poseStack,
                bufferSource, null, PACKED_LIGHT_COORDS, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
        bufferSource.endBatch();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a hologram of the multiblock at the given position.
     * 
     * @param minecraft the Minecraft instance
     * @param event the render event
     * @param level the level the multiblock is in
     * @param controllerPos the position of the multiblock controller
     * @param blueprint the blueprint of the multiblock
     * @param facing the direction the multiblock is facing
     */
    private static void renderBlueprintHologram(Minecraft minecraft, RenderLevelStageEvent event,
            Level level, BlockPos controllerPos, MultiBlockBlueprint blueprint, Direction facing) {
        // Measure the time since the last render
        long currentTime = System.currentTimeMillis();
        elapsedTime += (currentTime - lastTime) / 1000.0F;
        // TODO: Easing function for the scale?
        scale = Math.min(elapsedTime / (float) HOLOGRAM_ANIMATION_DURATION, 1.0F);
        lastTime = currentTime;

        PoseStack poseStack = event.getPoseStack();
        // Push once to save the origin
        poseStack.pushPose();
        // Translate to the render position
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        poseStack.translate(-camPos.x + controllerPos.getX(), -camPos.y + controllerPos.getY(),
                -camPos.z + controllerPos.getZ());

        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        // Set the render state
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // Get the rotated positions and block states of the blueprint
        BlockPos[] positions = blueprint.getRotatedPositions(facing);
        BlockState[] blockStates = blueprint.getStates();
        BlueprintCheckResult result =
                blueprint.check(level, controllerPos, level.getBlockState(controllerPos));
        ArrayList<Integer> wrongBlockIndices = result.getWrongBlockIndices();
        ArrayList<Integer> missingBlockIndices = result.getMissingBlockIndices();
        // Render the overlay on the wrong blocks
        renderHologramBlocks(poseStack, blockRenderer, bufferSource, positions, null,
                wrongBlockIndices, RED, false);
        // Find the missing blocks that are not in the wrong blocks
        ArrayList<Integer> airBlockIndices = new ArrayList<Integer>();
        airBlockIndices.addAll(missingBlockIndices);
        airBlockIndices.removeAll(wrongBlockIndices);
        // Render the hologram of the empty blocks
        renderHologramBlocks(poseStack, blockRenderer, bufferSource, positions, blockStates,
                airBlockIndices, WHITE, true);
        // Render the overlay on the empty blocks
        renderHologramBlocks(poseStack, blockRenderer, bufferSource, positions, null,
                airBlockIndices, CYAN, true);
        // Render the overlay on the controller so the player can see which one it is
        // TODO: Different way to highlight the controller. Right now it looks like it's also a
        // hologram, which it isn't necessarily
        float[] color = result.isValid() ? GREEN : YELLOW;
        renderHologramBlock(poseStack, blockRenderer, bufferSource, new BlockPos(0, 0, 0), null,
                color, true, false);
        // Reset shader color after rendering the hologram
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        ArrayList<ItemStack> missingBlockStacks =
                blueprint.getCombinedItemStacks(missingBlockIndices);
        ArrayList<ItemStack> wrongBlockStacks =
                blueprint.getCombinedItemStacks(wrongBlockIndices, level, controllerPos, facing);
        pushTopLeftPose(poseStack, null, facing);
        if (result.isValid()) {
            renderBlueprintComplete(poseStack, itemRenderer, minecraft.font, bufferSource);
        } else {
            // Render the itemstacks of the missing blocks
            renderBlueprintShoppingList(poseStack, itemRenderer, minecraft.font, bufferSource,
                    missingBlockStacks, wrongBlockStacks);
        }
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
    public static void setController(Level level, BlockPos pos) {
        // Only modify the renderer on the client side
        if (!level.isClientSide()) {
            return;
        }
        TestMod.LOGGER.debug("HologramRenderer::setController");
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof MultiBlockControllerBlock controller) {
            TestMod.LOGGER.debug("Setting controller: " + controller + " @ " + pos);
            HologramRenderer.level = level;
            controllerPos = pos;
            blueprint = controller.getBlueprint();
            facing = state.getValue(MultiBlockControllerBlock.FACING);
            elapsedTime = 0;
            lastTime = System.currentTimeMillis();
        }
    }

    /**
     * Check if the given position is the current controller for which to render the hologram.
     * 
     * @param pos the position to check
     */
    public static boolean isCurrentController(BlockPos pos) {
        TestMod.LOGGER.debug("HologramRenderer::isCurrentController");
        TestMod.LOGGER.debug("pos: " + pos);
        TestMod.LOGGER.debug("controllerPos: " + controllerPos);
        return controllerPos != null && controllerPos.equals(pos);
    }

    /**
     * Clear the controller for which to render the hologram. This is used when the multiblock
     * structure is formed or when the controller is destroyed.
     */
    public static void clearController() {
        TestMod.LOGGER.debug("HologramRenderer::clearController");
        level = null;
        controllerPos = null;
        blueprint = null;
        facing = null;
    }

    /**
     * Toggle the controller for which to render the hologram. If the given position is the current
     * controller, clear the controller. Otherwise, set the controller.
     * 
     * @param level the level the controller is in
     * @param pos the position of the controller
     */
    public static void toggleController(Level level, BlockPos pos) {
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
}
