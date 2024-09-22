package mwk.testmod.client.render.conduit;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.client.render.RenderUtils.Vertex;
import mwk.testmod.common.block.conduit.ConduitBlock;
import mwk.testmod.common.block.conduit.ConduitConnectionType;
import mwk.testmod.common.block.conduit.FluidConduitBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidConduitBlockEntityRenderer
        implements BlockEntityRenderer<FluidConduitBlockEntity> {

    // Offset fluid vertices to avoid z-fighting with the conduit block
    private static final float FLUID_OFFSET = 0.01F;

    private static final Vertex[] FLUID_VERTICES_CENTER = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET,
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET);
    // These cubes are used to render when the conduit connects to a block
    // Directions are in the same order as Direction.values()
    private static final Vertex[] FLUID_VERTICES_DOWN = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, 0 + FLUID_OFFSET,
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MIN - FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET);
    private static final Vertex[] FLUID_VERTICES_UP = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MAX + FLUID_OFFSET,
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET,
            1 - FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET);
    // temp public
    public static final Vertex[] FLUID_VERTICES_NORTH = RenderUtils.getCubeVertices(
            // private static final Vertex[] FLUID_VERTICES_NORTH = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET,
            0 + FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MIN - FLUID_OFFSET);
    private static final Vertex[] FLUID_VERTICES_SOUTH = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX + FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, 1 - FLUID_OFFSET);
    private static final Vertex[] FLUID_VERTICES_WEST = RenderUtils.getCubeVertices(
            0 + FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET,
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MIN - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET);
    private static final Vertex[] FLUID_VERTICES_EAST = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MAX + FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET,
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, 1 - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET);
    private static final Vertex[][] FLUID_VERTICES_DIRECTIONS =
            {FLUID_VERTICES_DOWN, FLUID_VERTICES_UP, FLUID_VERTICES_NORTH, FLUID_VERTICES_SOUTH,
                    FLUID_VERTICES_WEST, FLUID_VERTICES_EAST};
    // These cubes are used to render when the conduit connects to another conduit.
    // When rendering the connections between conduits, we want to avoid having a fluid face
    // in the middle of the conduit. To achieve this, we render one long fluid cube between
    // two conduits, instead of two separate fluid cubes.
    private static final Vertex[] FLUID_VERTICES_DOWN_UP = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET,
            1 + ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET);
    private static final Vertex[] FLUID_VERTICES_NORTH_SOUTH = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET,
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, 1 + ConduitBlock.CONDUIT_MIN + FLUID_OFFSET);
    private static final Vertex[] FLUID_VERTICES_WEST_EAST = RenderUtils.getCubeVertices(
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, ConduitBlock.CONDUIT_MAX - FLUID_OFFSET,
            ConduitBlock.CONDUIT_MAX - FLUID_OFFSET, 1 + ConduitBlock.CONDUIT_MIN + FLUID_OFFSET,
            ConduitBlock.CONDUIT_MIN + FLUID_OFFSET, ConduitBlock.CONDUIT_MIN + FLUID_OFFSET);
    private static final Vertex[][] FLUID_VERTICES_DIRECTIONS_COMBINED =
            {FLUID_VERTICES_DOWN_UP, FLUID_VERTICES_NORTH_SOUTH, FLUID_VERTICES_WEST_EAST};

    public FluidConduitBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FluidConduitBlockEntity conduitEntity, float partialTick,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight,
            int combinedOverlay) {
        // TODO: Make the conduit emit light based on the fluid (e.g. lava emits light)
        FluidStack fluidStack = conduitEntity.getFluidStack();
        // TODO: Would be cool if the fluid was animated
        renderFluidInConduit(conduitEntity, poseStack, multiBufferSource, fluidStack, combinedLight,
                combinedOverlay);
    }

    public static void renderFluidInConduit(FluidConduitBlockEntity conduitEntity,
            PoseStack poseStack, MultiBufferSource buffer, FluidStack fluidStack, int light,
            int overlay) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == Fluids.EMPTY) {
            return;
        }

        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite fluidStillSprite =
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                        .apply(renderProperties.getStillTexture());
        int color = renderProperties.getTintColor();

        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.translucent());
        BlockState state = conduitEntity.getBlockState();
        int connections = 0;
        for (int i = 0; i < FLUID_VERTICES_DIRECTIONS.length; i++) {
            ConduitConnectionType type = state.getValue(ConduitBlock.CONNECTOR_PROPERTIES[i]);
            if (type == ConduitConnectionType.NONE) {
                continue;
            }
            connections++;
            if (type.hasConnector()) {
                RenderUtils.renderCube(poseStack, vertexBuilder, FLUID_VERTICES_DIRECTIONS[i],
                        fluidStillSprite, color, light, overlay);
            }
            if (type == ConduitConnectionType.CONDUIT) {
                // To avoid rendering a fluid face in the middle of the conduit, we render
                // one long fluid cube between two conduits, instead of two separate fluid
                // cubes. This means that we only render the fluid cube for odd indices to avoid
                // rendering the same fluid cube twice
                if (i % 2 == 1) {
                    RenderUtils.renderCube(poseStack, vertexBuilder,
                            FLUID_VERTICES_DIRECTIONS_COMBINED[i / 2], fluidStillSprite, color,
                            light, overlay);
                }
            }
        }
        // Only render center if it isn't occluded on all sides
        if (connections < 6) {
            RenderUtils.renderCube(poseStack, vertexBuilder, FLUID_VERTICES_CENTER,
                    fluidStillSprite, color, light, overlay);
        }
    }


}
