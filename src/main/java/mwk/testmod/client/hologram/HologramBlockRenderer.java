package mwk.testmod.client.hologram;

import java.util.Collection;
import javax.annotation.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintBlockInfo;
import mwk.testmod.common.block.multiblock.controller.MultiBlockControllerBlock;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HologramBlockRenderer {

    private static final float HOLOGRAM_BLOCK_OVERLAY_ALPHA = 0.5F;

    private BlockRenderDispatcher blockRenderer;
    private BufferSource bufferSource;
    private HologramAnimation scaleAnimation;

    public HologramBlockRenderer(BlockRenderDispatcher blockRenderer, BufferSource bufferSource) {
        this.blockRenderer = blockRenderer;
        this.bufferSource = bufferSource;
        scaleAnimation = new HologramAnimation(HologramConfig.HOLOGRAM_ANIMATION_DURATION);
    }

    /**
     * Renders a hologram at the given position.
     * 
     * @param poseStack the pose stack
     * @param position the position of the hologram
     * @param state the block state of the hologram. If null the hologram overlay is rendered
     * @param facing the direction the blueprint is facing
     * @param color the color of the hologram. If null the current shader color is used
     * @param endBatch whether to end the current buffer source batch after rendering the hologram
     * @param animate whether to animate the hologram
     */
    public void renderHologramBlock(PoseStack poseStack, BlockPos position,
            @Nullable BlockState state, @Nullable Direction facing, @Nullable float[] color,
            boolean endBatch, boolean animate) {
        if (color != null) {
            RenderSystem.setShaderColor(color[0], color[1], color[2], HOLOGRAM_BLOCK_OVERLAY_ALPHA);
        }
        BlockState blockState;
        if (state != null) {
            // Make sure the controller is facing the right direction
            facing = facing != null ? facing : Direction.NORTH;
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
            float scale = scaleAnimation.getProgress();
            poseStack.translate(0.5F - scale / 2.0F, 0.5F - scale / 2.0F, 0.5F - scale / 2.0F);
            poseStack.scale(scale, scale, scale);
        }
        // TODO: Investigate batched rendering
        blockRenderer.renderSingleBlock(blockState, poseStack, bufferSource,
                HologramConfig.PACKED_LIGHT_COORDS, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        if (endBatch) {
            bufferSource.endBatch();
        }
    }

    public void renderHologramBlockOverlay(PoseStack poseStack, BlockPos position, float[] color,
            boolean endBatch, boolean animate) {
        renderHologramBlock(poseStack, position, null, null, color, endBatch, animate);
    }

    public void renderHologramBlocks(PoseStack poseStack, Collection<BlueprintBlockInfo> blocks,
            Direction facing, float[] color, boolean endBatch, boolean animate) {
        if (color != null) {
            RenderSystem.setShaderColor(color[0], color[1], color[2], HOLOGRAM_BLOCK_OVERLAY_ALPHA);
        }
        for (BlueprintBlockInfo block : blocks) {
            renderHologramBlock(poseStack, block.getRelativePosition(facing),
                    block.getExpectedState(), facing, color, false, animate);
        }
        if (endBatch) {
            bufferSource.endBatch();
        }
    }

    public void renderHologramBlockOverlays(PoseStack poseStack,
            Collection<BlueprintBlockInfo> blocks, Direction facing, float[] color,
            boolean endBatch, boolean animate) {
        if (color != null) {
            RenderSystem.setShaderColor(color[0], color[1], color[2], HOLOGRAM_BLOCK_OVERLAY_ALPHA);
        }
        for (BlueprintBlockInfo block : blocks) {
            renderHologramBlockOverlay(poseStack, block.getRelativePosition(facing), color, false,
                    animate);
        }
        if (endBatch) {
            bufferSource.endBatch();
        }
    }

    // TOOD: Not sure when/where to call this
    public void updateAnimation() {
        scaleAnimation.update();
    }

    public void startAnimation() {
        scaleAnimation.start();
    }
}
