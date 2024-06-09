package mwk.testmod.client.render.hologram.components;

import org.joml.Vector3f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HologramGeometryRenderer {

    private BufferSource bufferSource;

    public HologramGeometryRenderer(BufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    private Vector3f calculateNormal(Camera camera, Vector3f start, Vector3f end) {
        Vector3f lineDirection = new Vector3f();
        end.sub(start, lineDirection);
        lineDirection.normalize();

        Vec3 cameraPos = camera.getPosition();
        Vector3f cameraPosition =
                new Vector3f((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
        Vector3f toCamera = new Vector3f();
        cameraPosition.sub(start, toCamera);
        toCamera.normalize();

        Vector3f normal = new Vector3f();
        toCamera.cross(lineDirection, normal);
        normal.normalize();

        return normal;
    }

    public void drawLine(PoseStack poseStack, Camera camera, float x1, float y1, float z1, float x2,
            float y2, float z2, float r, float g, float b, float a) {
        PoseStack.Pose pose = poseStack.last();
        Vector3f start = new Vector3f(x1, y1, z1);
        Vector3f end = new Vector3f(x2, y2, z2);
        Vector3f normal = calculateNormal(camera, start, end);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        vertexConsumer.vertex(pose.pose(), x1, y1, z1).color(r, g, b, a)
                .normal(normal.x, normal.y, normal.z).endVertex();
        vertexConsumer.vertex(pose.pose(), x2, y2, z2).color(r, g, b, a)
                .normal(normal.x, normal.y, normal.z).endVertex();
    }

    public void drawLine(PoseStack poseStack, Camera camera, Vec3 start, Vec3 end, float r, float g,
            float b, float a) {
        drawLine(poseStack, camera, (float) start.x, (float) start.y, (float) start.z,
                (float) end.x, (float) end.y, (float) end.z, r, g, b, a);
    }

    public void drawLine(PoseStack poseStack, Camera camera, float x1, float y1, float z1, float x2,
            float y2, float z2, float[] color, float alpha) {
        drawLine(poseStack, camera, x1, y1, z1, x2, y2, z2, color[0], color[1], color[2], alpha);
    }

    public void drawLine(PoseStack poseStack, Camera camera, Vec3 start, Vec3 end, float[] color,
            float alpha) {
        drawLine(poseStack, camera, start, end, color[0], color[1], color[2], alpha);
    }

    private Vec3[] getBoxCorners(Vec3 min, Vec3 max) {
        return new Vec3[] {new Vec3(min.x, min.y, min.z), new Vec3(max.x, min.y, min.z),
                new Vec3(max.x, min.y, max.z), new Vec3(min.x, min.y, max.z),
                new Vec3(min.x, max.y, min.z), new Vec3(max.x, max.y, min.z),
                new Vec3(max.x, max.y, max.z), new Vec3(min.x, max.y, max.z)};
    }

    private Vec3[] getBoxCorners(AABB aabb) {
        return getBoxCorners(new Vec3(aabb.minX, aabb.minY, aabb.minZ),
                new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ));
    }

    public void drawBox(PoseStack poseStack, Camera camera, Vec3[] corners, float r, float g,
            float b, float a) {
        drawLine(poseStack, camera, corners[0], corners[1], r, g, b, a);
        drawLine(poseStack, camera, corners[1], corners[2], r, g, b, a);
        drawLine(poseStack, camera, corners[2], corners[3], r, g, b, a);
        drawLine(poseStack, camera, corners[3], corners[0], r, g, b, a);

        drawLine(poseStack, camera, corners[4], corners[5], r, g, b, a);
        drawLine(poseStack, camera, corners[5], corners[6], r, g, b, a);
        drawLine(poseStack, camera, corners[6], corners[7], r, g, b, a);
        drawLine(poseStack, camera, corners[7], corners[4], r, g, b, a);

        drawLine(poseStack, camera, corners[0], corners[4], r, g, b, a);
        drawLine(poseStack, camera, corners[1], corners[5], r, g, b, a);
        drawLine(poseStack, camera, corners[2], corners[6], r, g, b, a);
        drawLine(poseStack, camera, corners[3], corners[7], r, g, b, a);
    }

    public void drawBox(PoseStack poseStack, Camera camera, Vec3[] corners, float[] color,
            float alpha) {
        drawBox(poseStack, camera, corners, color[0], color[1], color[2], alpha);
    }

    public void drawBox(PoseStack poseStack, Camera camera, Vec3 min, Vec3 max, float r, float g,
            float b, float a) {
        Vec3[] vertices = getBoxCorners(min, max);
        drawBox(poseStack, camera, vertices, r, g, b, a);
    }

    public void drawBox(PoseStack poseStack, Camera camera, Vec3 min, Vec3 max, float[] color,
            float alpha) {
        drawBox(poseStack, camera, min, max, color[0], color[1], color[2], alpha);
    }

    public void drawBlockOutline(PoseStack poseStack, Camera camera, float[] color, float alpha) {
        drawBox(poseStack, camera, getBoxCorners(new Vec3(0, 0, 0), new Vec3(1, 1, 1)), color,
                alpha);
    }

    public void drawAABB(PoseStack poseStack, Camera camera, AABB aabb, float[] color,
            float alpha) {
        Vec3[] vertices = getBoxCorners(aabb);
        drawBox(poseStack, camera, vertices, color[0], color[1], color[2], alpha);
    }
}
