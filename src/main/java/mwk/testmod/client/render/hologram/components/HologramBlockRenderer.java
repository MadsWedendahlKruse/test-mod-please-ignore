package mwk.testmod.client.render.hologram.components;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import javax.annotation.Nullable;
import mwk.testmod.client.animations.FixedAnimationFloat;
import mwk.testmod.common.block.multiblock.HologramBlock;
import mwk.testmod.common.block.multiblock.HologramBlock.HologramColor;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintBlockInfo;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HologramBlockRenderer {

    private static final float ANIMATION_SCALE_DURATION = 0.3F;

    private final BlockRenderDispatcher blockRenderer;
    private final BufferSource bufferSource;
    private final FixedAnimationFloat scaleAnimation;

    public HologramBlockRenderer(BlockRenderDispatcher blockRenderer, BufferSource bufferSource) {
        this.blockRenderer = blockRenderer;
        this.bufferSource = bufferSource;
        scaleAnimation = new FixedAnimationFloat(ANIMATION_SCALE_DURATION,
                FixedAnimationFloat.Function.EASE_OUT_CUBIC);
    }

    /**
     * Renders a hologram at the given position.
     *
     * @param poseStack the pose stack
     * @param position  the position of the hologram
     * @param state     the block state of the hologram. If null the hologram overlay is rendered
     * @param animate   whether to animate the hologram
     */
    public void renderHologramBlock(PoseStack poseStack, BlockPos position,
            @Nullable BlockState state, boolean animate) {
        poseStack.pushPose();
        poseStack.translate(position.getX(), position.getY(), position.getZ());
        if (animate) {
            float scale = scaleAnimation.getValue();
            poseStack.translate(0.5F - scale / 2.0F, 0.5F - scale / 2.0F, 0.5F - scale / 2.0F);
            poseStack.scale(scale, scale, scale);
        }
        // TODO: Investigate batched rendering
        blockRenderer.renderSingleBlock(state, poseStack, bufferSource, LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    /**
     * Renders a hologram overlay at the given position. See
     * {@link #renderHologramBlock(PoseStack, BlockPos, BlockState, boolean)}
     */
    public void renderHologramBlockOverlay(PoseStack poseStack, BlockPos position,
            HologramColor color, boolean animate) {
        BlockState state = TestModBlocks.HOLOGRAM.get().defaultBlockState()
                .setValue(HologramBlock.COLOR, color);
        renderHologramBlock(poseStack, position, state, animate);
    }

    /**
     * Renders a collection of hologram blocks at the given position.
     *
     * @param poseStack the pose stack
     * @param blocks    the blocks to render@param animate whether to animate the hologram
     */
    public void renderHologramBlocks(PoseStack poseStack, Collection<BlueprintBlockInfo> blocks,
            boolean animate) {
        for (BlueprintBlockInfo block : blocks) {
            renderHologramBlock(poseStack, block.getRelativePosition(Direction.NORTH),
                    block.getExpectedState(), animate);
        }
    }

    /**
     * Renders a collection of hologram block overlays at the given position. See
     * {@link #renderHologramBlockOverlay(PoseStack, BlockPos, HologramColor, boolean)}
     */
    public void renderHologramBlockOverlays(PoseStack poseStack,
            Collection<BlueprintBlockInfo> blocks, HologramColor color, boolean animate) {
        for (BlueprintBlockInfo block : blocks) {
            renderHologramBlockOverlay(poseStack, block.getRelativePosition(Direction.NORTH), color,
                    animate);
        }
    }

    public void updateAnimation() {
        scaleAnimation.update();
    }

    public void startAnimation() {
        scaleAnimation.start();
    }
}
