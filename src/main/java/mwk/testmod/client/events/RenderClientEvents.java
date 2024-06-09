package mwk.testmod.client.events;

import java.util.List;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mwk.testmod.TestMod;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.client.render.RenderUtils.UnpackedQuad;
import mwk.testmod.client.render.RenderUtils.UnpackedVertex;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

/**
 * Class for various client-side events related to rendering.
 */
@Mod.EventBusSubscriber(modid = TestMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
public class RenderClientEvents {

    @SubscribeEvent
    public static void onRenderHighlightEvent(RenderHighlightEvent.Block event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        BlockPos pos = event.getTarget().getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof MultiBlockPartBlock multiBlockPart) {
            if (state.getValue(MultiBlockPartBlock.FORMED)) {
                // Cancel the original event so we can render our own highlight
                event.setCanceled(true);

                // TODO: There's no wireframe when looking at the controller
                BlockPos controllerPos = multiBlockPart.getControllerPos(level, pos);
                if (controllerPos == null) {
                    // If the controller position is null the block is the controller itself
                    controllerPos = pos;
                    return;
                }
                BlockState controllerState = level.getBlockState(controllerPos);
                MultiBlockControllerBlock controllerBlock =
                        (MultiBlockControllerBlock) controllerState.getBlock();

                PoseStack poseStack = event.getPoseStack();
                poseStack.pushPose();
                RenderUtils.setupWorldRenderPoseStack(poseStack, event.getCamera(), controllerPos);

                MultiBufferSource bufferSource = event.getMultiBufferSource();
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
                List<UnpackedQuad> quads = controllerBlock.getFormedQuads();
                // float[] offset = SeparatorBlockEntityRenderer.SPINNER_OFFSET;
                // poseStack.translate(offset[0], offset[1], offset[2]);

                float rotation = RenderUtils
                        .getRotation(controllerState.getValue(MultiBlockControllerBlock.FACING));
                // Rotate around the center of the block
                poseStack.rotateAround(new Quaternionf().rotationY(rotation), 0.5F, 0.5F, 0.5F);

                for (RenderUtils.UnpackedQuad quad : quads) {
                    for (int i = 0; i < 4; i++) {
                        UnpackedVertex vertex1 = quad.vertices()[i];
                        UnpackedVertex vertex2 = quad.vertices()[(i + 1) % 4];
                        // TODO: Include the normal in the quads
                        PoseStack.Pose pose = poseStack.last();
                        vertexConsumer.vertex(pose.pose(), vertex1.x(), vertex1.y(), vertex1.z())
                                .color(0, 0, 0, 0.5F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        vertexConsumer.vertex(pose.pose(), vertex2.x(), vertex2.y(), vertex2.z())
                                .color(0, 0, 0, 0.5F).normal(0.0F, 0.0F, 1.0F).endVertex();
                    }
                }
                poseStack.popPose();
            }
        }
    }
}
