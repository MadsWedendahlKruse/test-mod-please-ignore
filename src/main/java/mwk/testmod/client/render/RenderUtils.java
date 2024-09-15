package mwk.testmod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mwk.testmod.common.util.RandomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class RenderUtils {

    private RenderUtils() {
    }

    /**
     * Returns the rotation in radians for the given facing direction.
     *
     * @param facing the facing direction
     * @return the rotation in radians
     */
    public static float getRotation(Direction facing) {
        return switch (facing) {
            case EAST -> -(float) Math.PI / 2.0F;
            case WEST -> (float) Math.PI / 2.0F;
            case SOUTH -> (float) Math.PI;
            default -> 0.0F;
        };
    }

    public static void renderModel(PoseStack poseStack, VertexConsumer buffer, BakedModel model,
            int combinedLight, int combinedOverlay) {
        model.getQuads(null, null, RandomUtils.RANDOM_SOURCE).forEach(quad -> buffer
                .putBulkData(poseStack.last(), quad, 1, 1, 1, combinedLight, combinedOverlay));
    }

    /**
     * Represents a vertex with position and texture coordinates.
     */
    public record Vertex(float x, float y, float z, float u, float v) {

    }

    /**
     * Returns the vertices for a cube with the given dimensions.
     *
     * @param x1 the x coordinate of the first corner
     * @param y1 the y coordinate of the first corner
     * @param z1 the z coordinate of the first corner
     * @param x2 the x coordinate of the second corner
     * @param y2 the y coordinate of the second corner
     * @param z2 the z coordinate of the second corner
     * @return the vertices of the cube
     */
    public static Vertex[] getCubeVertices(float x1, float y1, float z1, float x2, float y2,
            float z2) {
        return new Vertex[]{
                // Front face
                new Vertex(x1, y1, z1, 0, 0), new Vertex(x1, y2, z1, 0, 1),
                new Vertex(x2, y2, z1, 1, 1), new Vertex(x2, y1, z1, 1, 0),
                // Back face
                new Vertex(x2, y1, z2, 0, 0), new Vertex(x2, y2, z2, 0, 1),
                new Vertex(x1, y2, z2, 1, 1), new Vertex(x1, y1, z2, 1, 0),
                // Left face
                new Vertex(x1, y1, z2, 0, 0), new Vertex(x1, y2, z2, 0, 1),
                new Vertex(x1, y2, z1, 1, 1), new Vertex(x1, y1, z1, 1, 0),
                // Right face
                new Vertex(x2, y1, z1, 0, 0), new Vertex(x2, y2, z1, 0, 1),
                new Vertex(x2, y2, z2, 1, 1), new Vertex(x2, y1, z2, 1, 0),
                // Top face
                new Vertex(x1, y2, z1, 0, 0), new Vertex(x1, y2, z2, 0, 1),
                new Vertex(x2, y2, z2, 1, 1), new Vertex(x2, y2, z1, 1, 0),
                // Bottom face
                new Vertex(x1, y1, z2, 0, 0), new Vertex(x1, y1, z1, 0, 1),
                new Vertex(x2, y1, z1, 1, 1), new Vertex(x2, y1, z2, 1, 0)};
    }

    /**
     * Renders a cube with the given vertices, sprite, color, light, and overlay.
     *
     * @param poseStack     the pose stack to render with
     * @param vertexBuilder the vertex consumer to render with
     * @param vertices      the vertices of the cube
     * @param sprite        the sprite to render
     * @param color         the color to render with
     * @param light         the light level to render with
     * @param overlay       the overlay to render with
     */
    public static void renderCube(PoseStack poseStack, VertexConsumer vertexBuilder,
            Vertex[] vertices, TextureAtlasSprite sprite, int color, int light, int overlay) {
        for (int i = 0; i < vertices.length; i += 4) {
            drawQuad(vertexBuilder, poseStack, sprite, vertices[i], vertices[i + 1],
                    vertices[i + 2], vertices[i + 3], color, light, overlay);
        }
    }

    /**
     * Renders a quad with the given vertices, sprite, color, light, and overlay.
     *
     * @param vertexBuilder the vertex consumer to render with
     * @param poseStack     the pose stack to render with
     * @param sprite        the sprite to render
     * @param v1            the first vertex
     * @param v2            the second vertex
     * @param v3            the third vertex
     * @param v4            the fourth vertex
     * @param color         the color to render with
     * @param light         the light level to render with
     * @param overlay       the overlay to render with
     */
    public static void drawQuad(VertexConsumer vertexBuilder, PoseStack poseStack,
            TextureAtlasSprite sprite, Vertex v1, Vertex v2, Vertex v3, Vertex v4, int color,
            int light, int overlay) {
        vertex(vertexBuilder, poseStack, sprite, v1, color, light, overlay);
        vertex(vertexBuilder, poseStack, sprite, v2, color, light, overlay);
        vertex(vertexBuilder, poseStack, sprite, v3, color, light, overlay);
        vertex(vertexBuilder, poseStack, sprite, v4, color, light, overlay);
    }

    /**
     * Renders a vertex with the given sprite, color, light, and overlay.
     *
     * @param vertexBuilder the vertex consumer to render with
     * @param poseStack     the pose stack to render with
     * @param sprite        the sprite to render
     * @param vertex        the vertex to render
     * @param color         the color to render with
     * @param light         the light level to render with
     * @param overlay       the overlay to render with
     */
    public static void vertex(VertexConsumer vertexBuilder, PoseStack poseStack,
            TextureAtlasSprite sprite, Vertex vertex, int color, int light, int overlay) {
        // TODO: apparently the normal is determined by the order of the vertices,
        // even though the vertex consumer takes a normal as an argument?
        vertexBuilder.vertex(poseStack.last().pose(), vertex.x(), vertex.y(), vertex.z())
                .color(color).uv(sprite.getU(vertex.u()), sprite.getV(vertex.v()))
                .overlayCoords(overlay).uv2(light).normal(0, 0, 0).endVertex();
    }

    /**
     * Returns the texture for the given fluid.
     *
     * @param fluid   the fluid to get the texture for
     * @param flowing whether to get the flowing texture or the still texture
     * @return the texture for the fluid
     */
    public static TextureAtlasSprite getFluidTexture(Fluid fluid, boolean flowing) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation texture =
                flowing ? renderProperties.getFlowingTexture() : renderProperties.getStillTexture();
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
    }

    /**
     * Returns the texture for the given fluid stack.
     *
     * @param fluidStack the fluid stack to get the texture for
     * @param flowing    whether to get the flowing texture or the still texture
     * @return the texture for the fluid stack
     */
    public static TextureAtlasSprite getFluidTexture(FluidStack fluidStack, boolean flowing) {
        return getFluidTexture(fluidStack.getFluid(), flowing);
    }
}
