package mwk.testmod.client.render;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.TestMod;
import mwk.testmod.common.util.RandomUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class RenderUtils {

    private RenderUtils() {}

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
     * Sets up the pose stack to render starting at the given position in the world.
     *
     * @param poseStack the pose stack to set up
     * @param camera the camera to render from
     * @param pos the position to render at
     */
    public static void setupWorldRenderPoseStack(PoseStack poseStack, Camera camera, Vec3 pos) {
        Vec3 cameraPos = camera.getPosition();
        poseStack.translate(pos.x - cameraPos.x(), pos.y - cameraPos.y(), pos.z - cameraPos.z());
    }

    /**
     * {@link #setupWorldRenderPoseStack(PoseStack, Camera, Vec3)}
     */
    public static void setupWorldRenderPoseStack(PoseStack poseStack, Camera camera, BlockPos pos) {
        setupWorldRenderPoseStack(poseStack, camera, new Vec3(pos.getX(), pos.getY(), pos.getZ()));
    }

    // TODO: I can't really tell if this sections is just re-inventing the wheel

    /**
     * Helper record to represent a vertex unpacked from BakedQuad.
     */
    public static record UnpackedVertex(float x, float y, float z) {
    }

    /**
     * Helper record to represent a quad unpacked from BakedModel.
     */
    public static record UnpackedQuad(UnpackedVertex[] vertices) {
    }

    private static final int VERTEX_PER_QUAD = 4;

    /**
     * Unpacks the quads from a baked model into a list of {@link UnpackedQuad}.
     *
     * @param model the model to unpack the quads from
     * @return a list of unpacked quads
     */
    public static List<UnpackedQuad> unpackQuads(BakedModel model) {
        List<BakedQuad> quads = model.getQuads(null, null, RandomUtils.RANDOM_SOURCE);
        List<UnpackedQuad> unpackedQuads = new ArrayList<>();
        for (BakedQuad quad : quads) {
            int[] vertexData = quad.getVertices();
            int dataSize = vertexData.length / VERTEX_PER_QUAD;
            UnpackedVertex[] vertices = new UnpackedVertex[VERTEX_PER_QUAD];
            for (int i = 0; i < vertexData.length; i += dataSize) {
                float x = Float.intBitsToFloat(vertexData[i]);
                float y = Float.intBitsToFloat(vertexData[i + 1]);
                float z = Float.intBitsToFloat(vertexData[i + 2]);
                vertices[i / dataSize] = new UnpackedVertex(x, y, z);
            }
            unpackedQuads.add(new UnpackedQuad(vertices));
        }
        return unpackedQuads;
    }

    // TODO: This doesn't actually work
    // private static final int GRID_SIZE = 8;
    // private static final float GRID_RESOLUTION = 1.0f / GRID_SIZE;
    // private boolean[][][] voxelGrid = new boolean[GRID_SIZE][GRID_SIZE][GRID_SIZE];

    // private void voxelizeTriangle(Vector3f v1, Vector3f v2, Vector3f v3) {
    // // Determine the bounding box of the triangle
    // int minX = (int) Math.floor(Math.min(v1.x, Math.min(v2.x, v3.x)) * GRID_SIZE);
    // int minY = (int) Math.floor(Math.min(v1.y, Math.min(v2.y, v3.y)) * GRID_SIZE);
    // int minZ = (int) Math.floor(Math.min(v1.z, Math.min(v2.z, v3.z)) * GRID_SIZE);
    // int maxX = (int) Math.ceil(Math.max(v1.x, Math.max(v2.x, v3.z)) * GRID_SIZE);
    // int maxY = (int) Math.ceil(Math.max(v1.y, Math.max(v2.y, v3.y)) * GRID_SIZE);
    // int maxZ = (int) Math.ceil(Math.max(v1.z, Math.max(v2.z, v3.z)) * GRID_SIZE);

    // // Clamp the bounding box to the grid dimensions
    // minX = Math.max(minX, 0);
    // minY = Math.max(minY, 0);
    // minZ = Math.max(minZ, 0);
    // maxX = Math.min(maxX, GRID_SIZE - 1);
    // maxY = Math.min(maxY, GRID_SIZE - 1);
    // maxZ = Math.min(maxZ, GRID_SIZE - 1);

    // // Iterate over the bounding box and mark the voxels that intersect the triangle
    // for (int x = minX; x <= maxX; x++) {
    // for (int y = minY; y <= maxY; y++) {
    // for (int z = minZ; z <= maxZ; z++) {
    // if (intersectsTriangle(x * GRID_RESOLUTION, y * GRID_RESOLUTION,
    // z * GRID_RESOLUTION, GRID_RESOLUTION, v1, v2, v3)) {
    // voxelGrid[x][y][z] = true;
    // }
    // }
    // }
    // }
    // }

    // private static boolean intersectsTriangle(float x, float y, float z, float size, Vector3f v1,
    // Vector3f v2, Vector3f v3) {
    // // Check if the voxel intersects with the triangle
    // // This is a placeholder implementation; you can use a more accurate method if needed
    // float centerX = x + size / 2;
    // float centerY = y + size / 2;
    // float centerZ = z + size / 2;
    // return pointInTriangle(centerX, centerY, centerZ, v1, v2, v3);
    // }

    // private static boolean pointInTriangle(float px, float py, float pz, Vector3f v1, Vector3f
    // v2,
    // Vector3f v3) {
    // // Check if the point is inside the triangle using barycentric coordinates
    // float dX = px - v3.x;
    // float dY = py - v3.y;
    // float dZ = pz - v3.z;
    // float dX21 = v3.x - v2.x;
    // float dY12 = v2.y - v3.y;
    // float dZ13 = v3.z - v1.z;
    // float D = dY12 * (v1.x - v3.x) + dX21 * (v1.y - v3.y);
    // float s = dY12 * dX + dX21 * dY;
    // float t = (v3.y - v1.y) * dX + dZ13 * dY;
    // if ((s < 0) != (t < 0) || (D == 0))
    // return false;
    // float a = D + s + t;
    // return D < 0 ? a <= 0 && s >= 0 && t >= 0 : a >= 0 && s <= 0 && t <= 0;
    // }

    // private void printVoxelGrid() {
    // TestMod.LOGGER.debug(modelPath + " voxel grid:");
    // for (int z = 0; z < GRID_SIZE; z++) {
    // StringBuilder row = new StringBuilder();
    // row.append("Layer ").append(z).append(":\n");
    // for (int y = 0; y < GRID_SIZE; y++) {
    // for (int x = 0; x < GRID_SIZE; x++) {
    // row.append(voxelGrid[x][y][z] ? '#' : '.');
    // }
    // row.append('\n');
    // }
    // TestMod.LOGGER.debug(row.toString());
    // }
    // }
}
