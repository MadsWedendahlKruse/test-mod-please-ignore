package mwk.testmod.client.render;

import org.codehaus.plexus.util.dag.Vertex;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mwk.testmod.TestMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class RenderUtils {

    private RenderUtils() {}

    /**
     * Returns the rotation in radians for the given facing direction.
     *
     * @param facing the facing direction
     * @return the rotation in radians
     */
    public static float getRotation(Direction facing) {
        switch (facing) {
            case EAST:
                return -(float) Math.PI / 2.0F;
            case WEST:
                return (float) Math.PI / 2.0F;
            case SOUTH:
                return (float) Math.PI;
            case NORTH:
            default:
                return 0.0F;
        }
    }

    public static final ResourceLocation ITEM_SLOT_SPRITE =
            new ResourceLocation(TestMod.MODID, "widget/item_slot");
    public static final int ITEM_SLOT_SIZE = 18;

    /**
     * Renders an item slot sprite at the given position. The position is the top-left corner of the
     * item slot, meaning the border sprite will be rendered 1 pixel to the left and 1 pixel above
     * the given position. The width and height can be used to render e.g. the border of an energy
     * bar
     *
     * @param guiGraphics the graphics object to render with
     * @param x the x position to render the item slot at
     * @param y the y position to render the item slot at
     * @param width the width of the item slot
     * @param height the height of the item slot
     */
    public static void renderItemSlot(GuiGraphics guiGraphics, int x, int y, int width,
            int height) {
        // -1 to account for the border of the item slot sprite
        guiGraphics.blitSprite(ITEM_SLOT_SPRITE, x - 1, y - 1, width, height);
    }

    /**
     * Renders an item slot with the default size of 18x18 at the given position. See
     * {@link #renderItemSlot(GuiGraphics, int, int, int, int)}
     */
    public static void renderItemSlot(GuiGraphics guiGraphics, int x, int y) {
        renderItemSlot(guiGraphics, x, y, ITEM_SLOT_SIZE, ITEM_SLOT_SIZE);
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
        return new Vertex[] {
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
     * @param poseStack the pose stack to render with
     * @param vertexBuilder the vertex consumer to render with
     * @param vertices the vertices of the cube
     * @param sprite the sprite to render
     * @param color the color to render with
     * @param light the light level to render with
     * @param overlay the overlay to render with
     */
    public static void renderCube(PoseStack poseStack, VertexConsumer vertexBuilder,
            Vertex[] vertices, TextureAtlasSprite sprite, int color, int light, int overlay) {
        poseStack.pushPose();
        for (int i = 0; i < vertices.length; i += 4) {
            drawQuad(vertexBuilder, poseStack, sprite, vertices[i], vertices[i + 1],
                    vertices[i + 2], vertices[i + 3], color, light, overlay);
        }
        poseStack.popPose();
    }

    /**
     * Renders a quad with the given vertices, sprite, color, light, and overlay.
     *
     * @param vertexBuilder the vertex consumer to render with
     * @param poseStack the pose stack to render with
     * @param sprite the sprite to render
     * @param v1 the first vertex
     * @param v2 the second vertex
     * @param v3 the third vertex
     * @param v4 the fourth vertex
     * @param color the color to render with
     * @param light the light level to render with
     * @param overlay the overlay to render with
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
     * @param poseStack the pose stack to render with
     * @param sprite the sprite to render
     * @param vertex the vertex to render
     * @param color the color to render with
     * @param light the light level to render with
     * @param overlay the overlay to render with
     */
    public static void vertex(VertexConsumer vertexBuilder, PoseStack poseStack,
            TextureAtlasSprite sprite, Vertex vertex, int color, int light, int overlay) {
        // TODO: apparently the normal is determined by the order of the vertices,
        // even though the vertex consumer takes a normal as an argument?
        vertexBuilder.vertex(poseStack.last().pose(), vertex.x(), vertex.y(), vertex.z())
                .color(color).uv(sprite.getU(vertex.u()), sprite.getV(vertex.v()))
                .overlayCoords(overlay).uv2(light).normal(0, 0, 0).endVertex();
    }
}
