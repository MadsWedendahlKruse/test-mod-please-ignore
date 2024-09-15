package mwk.testmod.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;

/**
 * Base class for rendering block entities of multiblock structures.
 */
public abstract class MultiBlockEntityRenderer<T extends BlockEntity>
        implements BlockEntityRenderer<T> {

    /**
     * Base render method for checking block state and rotating the block entity. This then calls
     * the abstract method renderDynamicParts to render the dynamic parts of the multiblock.
     */
    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof MultiBlockControllerBlock)) {
            return;
        }
        if (!blockState.getValue(MultiBlockControllerBlock.FORMED)) {
            return;
        }
        Direction facing = blockState.getValue(MultiBlockControllerBlock.FACING);
        float facingAngle = RenderUtils.getRotation(facing);
        poseStack.pushPose();
        // Rotate around the center of the block
        poseStack.rotateAround(new Quaternionf().rotationY(facingAngle), 0.5F, 0.5F, 0.5F);

        renderDynamicParts(blockEntity, partialTick, poseStack, multiBufferSource, combinedLight,
                combinedOverlay);

        poseStack.popPose();
    }

    /**
     * Abstract method to render the dynamic parts of the multiblock, e.g. the rotor of a crusher.
     */
    protected abstract void renderDynamicParts(T blockEntity, float partialTick,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight,
            int combinedOverlay);

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        BlockState blockState = blockEntity.getBlockState();
        if (blockState.getBlock() instanceof MultiBlockControllerBlock controllerBlock) {
            MultiBlockBlueprint blueprint = controllerBlock.getBlueprint();
            if (blueprint != null) {
                return blueprint.getAABB(blockEntity.getBlockPos(),
                        blockState.getValue(MultiBlockControllerBlock.FACING));
            }
        }
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }
}
