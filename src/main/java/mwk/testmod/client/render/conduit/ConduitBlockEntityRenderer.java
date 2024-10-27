package mwk.testmod.client.render.conduit;

import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.client.render.hologram.components.HologramGeometryRenderer;
import mwk.testmod.common.block.conduit.ConduitBlock;
import mwk.testmod.common.block.conduit.ConduitBlockEntity;
import mwk.testmod.common.block.conduit.network.base.ConduitNetwork;
import mwk.testmod.common.block.conduit.network.base.ConduitNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Only used for debugging purposes.
 */
public class ConduitBlockEntityRenderer<T extends ConduitBlockEntity<?>> implements
        BlockEntityRenderer<T> {

    private static final float[][] COLORS = new float[][]{
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1},
            {1, 1, 0},
            {1, 0, 1},
            {0, 1, 1},
            {1, 1, 1},
            {1, 0.5F, 0},
            {1, 0, 0.5F},
            {0.5F, 1, 0},
            {0, 1, 0.5F},
            {0.5F, 0, 1},
            {0, 0.5F, 1},
            {0.5F, 0.5F, 1},
            {0.5F, 1, 0.5F},
            {1, 0.5F, 0.5F},
            {0.5F, 0.5F, 0},
            {0, 0.5F, 0.5F},
            {0.5F, 0, 0.5F},
            {0.5F, 0.5F, 0.5F}
    };

    private final Minecraft minecraft;
    private final HologramGeometryRenderer geometryRenderer;

    public ConduitBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.minecraft = Minecraft.getInstance();
        BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        geometryRenderer = new HologramGeometryRenderer(bufferSource);
    }

    @Override
    public void render(T conduitBlockEntity, float partialTick,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight,
            int combinedOverlay) {
        if (!minecraft.gui.getDebugOverlay().showDebugScreen()) {
            return;
        }
        ConduitNetwork<?, ?> network = ConduitNetworkManager.getInstance()
                .getNetwork(conduitBlockEntity.getBlockPos());
        if (conduitBlockEntity.getBlockState().getBlock() instanceof ConduitBlock conduitBlock) {
            VoxelShape shape = conduitBlock.getShape(conduitBlockEntity.getBlockState(),
                    conduitBlockEntity.getLevel(), conduitBlockEntity.getBlockPos(), null);
            int colorIdx = System.identityHashCode(network) % COLORS.length;
            geometryRenderer.drawShape(poseStack, shape, COLORS[colorIdx], 1.0F);
        }
    }
}
