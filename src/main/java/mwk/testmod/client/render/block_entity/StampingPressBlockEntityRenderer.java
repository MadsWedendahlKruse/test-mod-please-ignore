package mwk.testmod.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.common.block.entity.StampingPressBlockEntity;
import mwk.testmod.init.registries.TestModModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class StampingPressBlockEntityRenderer extends
        MultiBlockEntityRenderer<StampingPressBlockEntity> {

    public static final float[] PISTON_OFFSET = {0F, 1.75F, 1.0F};
    public static final float[] STAMPING_DIE_OFFSET = {0.5F, -0.5F / 16, 0.5F};
    public static final float[] CONVEYOR_OFFSET = {0.5F, 1.0F, 1.5F};
    public static final float CONVEYOR_MAX_POSITION = 1.5F;
    public static final Quaternionf ROT_X_90 = new Quaternionf().rotationX((float) Math.PI / 2);
    private final ItemRenderer itemRenderer;

    public StampingPressBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        itemRenderer = minecraft.getItemRenderer();
    }

    @Override
    protected void renderDynamicParts(StampingPressBlockEntity blockEntity, float partialTick,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight,
            int combinedOverlay) {
        if (blockEntity.isWorking()) {
            blockEntity.updateAnimations();
        }
        float pistonHeight = blockEntity.getPistonHeight();
        renderPiston(poseStack, multiBufferSource, combinedLight, combinedOverlay,
                pistonHeight);
        // Render the stamping die
        ItemStack stampingDie = blockEntity.getStampingDie();
        renderStampingDie(poseStack, multiBufferSource, combinedLight, combinedOverlay,
                stampingDie, pistonHeight);
        // Render input/output item on the conveyor
        float conveyorPosition = blockEntity.getConveyorPosition();
        // Conveyor position is +/- some distance from the center where the item is stamped
        // If the item made it to the center, we want to render the output instead
        ItemStack item =
                conveyorPosition >= 0 ? blockEntity.getOutput() : blockEntity.getInput();
        renderConveyor(poseStack, multiBufferSource, combinedLight, combinedOverlay, item,
                conveyorPosition);
    }

    private void renderPiston(PoseStack poseStack, MultiBufferSource multiBufferSource,
            int combinedLight, int combinedOverlay, float pistonHeight) {
        poseStack.pushPose();
        poseStack.translate(PISTON_OFFSET[0], PISTON_OFFSET[1] + pistonHeight,
                PISTON_OFFSET[2]);
        VertexConsumer buffer = multiBufferSource.getBuffer(RenderType.solid());
        RenderUtils.renderModel(poseStack, buffer,
                TestModModels.STAMPING_PRESS_PISTON.getBakedModel(), combinedLight,
                combinedOverlay);
        poseStack.popPose();
    }

    private void renderStampingDie(PoseStack poseStack, MultiBufferSource multiBufferSource,
            int combinedLight, int combinedOverlay, ItemStack stampingDie, float pistonHeight) {
        poseStack.pushPose();
        poseStack.translate(PISTON_OFFSET[0] + STAMPING_DIE_OFFSET[0],
                PISTON_OFFSET[1] + STAMPING_DIE_OFFSET[1] + pistonHeight,
                PISTON_OFFSET[2] + STAMPING_DIE_OFFSET[2]);
        poseStack.mulPose(ROT_X_90);
        itemRenderer.renderStatic(stampingDie, ItemDisplayContext.NONE, combinedLight,
                combinedOverlay, poseStack, multiBufferSource, null, 0);
        poseStack.popPose();
    }

    private void renderConveyor(PoseStack poseStack, MultiBufferSource multiBufferSource,
            int combinedLight, int combinedOverlay, ItemStack item, float conveyorPosition) {
        poseStack.pushPose();
        poseStack.translate(CONVEYOR_OFFSET[0] - conveyorPosition, CONVEYOR_OFFSET[1],
                CONVEYOR_OFFSET[2]);
        poseStack.mulPose(ROT_X_90);
        if (Math.abs(conveyorPosition) > 1) {
            // This is some low-key cursed math to scale the item based on its position on the conveyor
            // Basically I don't want the item to just blink out of existence when it's at the end of the
            // conveyor, so once it moves past a certain point (1 block), it starts to shrink
            // The math is so that the item is at 0.5 scale when it's 0.5 blocks past the "end"
            float scale =
                    (CONVEYOR_MAX_POSITION - Math.abs(conveyorPosition)) /
                            (CONVEYOR_MAX_POSITION - 1);
            poseStack.scale(scale, scale, scale);
        }
        itemRenderer.renderStatic(item, ItemDisplayContext.NONE, combinedLight, combinedOverlay,
                poseStack, multiBufferSource, null, 0);
        poseStack.popPose();
    }
}
