package mwk.testmod.client.render.block_entity;

import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mwk.testmod.common.block.entity.SeparatorBlockEntity;
import mwk.testmod.common.util.RandomUtils;
import mwk.testmod.init.registries.TestModModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;

public class SeparatorBlockEntityRenderer extends MultiBlockEntityRenderer<SeparatorBlockEntity> {

    public static final float[] SPINNER_OFFSET = new float[] {-0.5F, 0.75F, 1.5F};

    public SeparatorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderDynamicParts(SeparatorBlockEntity blockEntity, float partialTick,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight,
            int combinedOverlay) {
        if (blockEntity.isWorking()) {
            blockEntity.updateSpinnerAnimation();
        }
        BakedModel spinnerModel = TestModModels.SEPARATOR_SPINNER.getBakedModel();
        poseStack.pushPose();
        poseStack.translate(SPINNER_OFFSET[0], SPINNER_OFFSET[1], SPINNER_OFFSET[2]);
        poseStack.mulPose(new Quaternionf().rotationY(blockEntity.getSpinnerAngle()));
        VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.solid());
        spinnerModel.getQuads(null, null, RandomUtils.RANDOM_SOURCE).forEach(quad -> buffer
                .putBulkData(poseStack.last(), quad, 1, 1, 1, combinedLight, combinedOverlay));
        poseStack.popPose();
    }

}
