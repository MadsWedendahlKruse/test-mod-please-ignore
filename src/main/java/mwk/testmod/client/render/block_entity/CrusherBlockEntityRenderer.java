package mwk.testmod.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.common.block.entity.CrusherBlockEntity;
import mwk.testmod.init.registries.TestModModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import org.joml.Quaternionf;

public class CrusherBlockEntityRenderer extends MultiBlockEntityRenderer<CrusherBlockEntity> {

    // Offset of the rotors relative to the block entity
    // Rotors are only two blocks wide, so we need two of them on each side
    public static final float[] ROTOR_OFFSET_FRONT_1 = {-0.5F, 2, 1};
    public static final float[] ROTOR_OFFSET_FRONT_2 = {1.5F, 2, 1};
    public static final float[] ROTOR_OFFSET_BACK_1 = {-0.5F, 2, 2};
    public static final float[] ROTOR_OFFSET_BACK_2 = {1.5F, 2, 2};
    public static final float ROTOR_ROT_FRONT_Y = (float) Math.PI / 2;
    public static final float ROTOR_ROT_BACK_Y = -(float) Math.PI / 2;

    public CrusherBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderDynamicParts(CrusherBlockEntity crusherEntity, float partialTick,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight,
            int combinedOverlay) {
        if (crusherEntity.isWorking()) {
            crusherEntity.updateRotorAngle();
        }
        BakedModel rotorModel = TestModModels.CRUSHER_ROTOR.getBakedModel();
        renderRotor(poseStack, multiBufferSource, rotorModel, ROTOR_OFFSET_FRONT_1,
                ROTOR_ROT_FRONT_Y, crusherEntity.getRotorAngle(), combinedLight, combinedOverlay);
        renderRotor(poseStack, multiBufferSource, rotorModel, ROTOR_OFFSET_FRONT_2,
                ROTOR_ROT_FRONT_Y, crusherEntity.getRotorAngle(), combinedLight, combinedOverlay);
        renderRotor(poseStack, multiBufferSource, rotorModel, ROTOR_OFFSET_BACK_1, ROTOR_ROT_BACK_Y,
                crusherEntity.getRotorAngle(), combinedLight, combinedOverlay);
        renderRotor(poseStack, multiBufferSource, rotorModel, ROTOR_OFFSET_BACK_2, ROTOR_ROT_BACK_Y,
                crusherEntity.getRotorAngle(), combinedLight, combinedOverlay);
    }

    private void renderRotor(PoseStack poseStack, MultiBufferSource multiBufferSource,
            BakedModel rotorModel, float[] offset, float rotY, float angle, int combinedLight,
            int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(offset[0], offset[1], offset[2]);
        poseStack.mulPose(new Quaternionf().rotationY(rotY));
        poseStack.mulPose(new Quaternionf().rotationZ(angle));
        VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.solid());
        RenderUtils.renderModel(poseStack, buffer, rotorModel, combinedLight, combinedOverlay);
        poseStack.popPose();
    }
}
