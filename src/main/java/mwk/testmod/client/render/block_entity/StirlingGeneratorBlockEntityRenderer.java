package mwk.testmod.client.render.block_entity;

import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.common.block.entity.StirlingGeneratorBlockEntity;
import mwk.testmod.init.registries.TestModModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;

public class StirlingGeneratorBlockEntityRenderer
        extends MultiBlockEntityRenderer<StirlingGeneratorBlockEntity> {

    private static final float[] FLYWHEEL_OFFSET = {16, 19.125F, 6};
    private static final float FLYWHEEL_ROT_Y = -(float) Math.PI / 2;
    private static final float[] PISTON_1_OFFSET = {6, 26, 13};
    private static final float[] PISTON_2_OFFSET = {6, 26, 23};
    private static final float[] PISTON_3_OFFSET = {26, 26, 13};
    private static final float[] PISTON_4_OFFSET = {26, 26, 23};
    private static final float PISTON_Z_ROT = (float) Math.PI / 4;
    // Offset from the piston's initial position
    private static final float PISTON_HEIGHT_MAX = 3F / 16F;

    public StirlingGeneratorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderDynamicParts(StirlingGeneratorBlockEntity generatorEntity,
            float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource,
            int combinedLight, int combinedOverlay) {
        if (generatorEntity.isWorking()) {
            generatorEntity.updateFlywheelAnimation();
        }
        float flywheelAngle = generatorEntity.getFlywheelAngle();
        BakedModel flywheelModel = TestModModels.STIRLING_GENERATOR_FLYWHEEL.getBakedModel();
        renderFlywheel(poseStack, multiBufferSource, flywheelModel, flywheelAngle, combinedLight,
                combinedOverlay);
        BakedModel pistonModel = TestModModels.STIRLING_GENERATOR_PISTON.getBakedModel();
        float pistonUpHeight = (float) Math.sin(flywheelAngle) * PISTON_HEIGHT_MAX;
        float pistonDownHeight = (float) Math.sin(flywheelAngle + Math.PI) * PISTON_HEIGHT_MAX;
        renderPiston(poseStack, multiBufferSource, pistonModel, PISTON_1_OFFSET, PISTON_Z_ROT,
                pistonUpHeight, combinedLight, combinedOverlay);
        renderPiston(poseStack, multiBufferSource, pistonModel, PISTON_2_OFFSET, PISTON_Z_ROT,
                pistonDownHeight, combinedLight, combinedOverlay);
        renderPiston(poseStack, multiBufferSource, pistonModel, PISTON_3_OFFSET, -PISTON_Z_ROT,
                pistonDownHeight, combinedLight, combinedOverlay);
        renderPiston(poseStack, multiBufferSource, pistonModel, PISTON_4_OFFSET, -PISTON_Z_ROT,
                pistonUpHeight, combinedLight, combinedOverlay);
    }

    private void renderFlywheel(PoseStack poseStack, MultiBufferSource multiBufferSource,
            BakedModel flywheelModel, float angle, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(FLYWHEEL_OFFSET[0] / 16, FLYWHEEL_OFFSET[1] / 16,
                FLYWHEEL_OFFSET[2] / 16);
        poseStack.mulPose(new Quaternionf().rotationY(FLYWHEEL_ROT_Y));
        poseStack.mulPose(new Quaternionf().rotationX(-angle));
        VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.solid());
        RenderUtils.renderModel(poseStack, buffer, flywheelModel, combinedLight, combinedOverlay);
        poseStack.popPose();
    }

    private void renderPiston(PoseStack poseStack, MultiBufferSource multiBufferSource,
            BakedModel pistonModel, float[] offset, float rotZ, float pistonHeight,
            int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(offset[0] / 16, offset[1] / 16, offset[2] / 16);
        poseStack.mulPose(new Quaternionf().rotationZ(rotZ));
        poseStack.translate(0, pistonHeight, 0);
        VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.solid());
        RenderUtils.renderModel(poseStack, buffer, pistonModel, combinedLight, combinedOverlay);
        poseStack.popPose();
    }

}
