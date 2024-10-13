package mwk.testmod.client.render.hologram.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HologramGeometryRenderer {

    private BufferSource bufferSource;

    public HologramGeometryRenderer(BufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    public void drawLine(PoseStack poseStack, float x1, float y1, float z1, float x2, float y2,
            float z2, float r, float g, float b, float a) {
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
        vertexConsumer.addVertex(pose.pose(), x1, y1, z1).setColor(r, g, b, a)
                .setNormal(0.0F, 0.0F, 1.0F);
        vertexConsumer.addVertex(pose.pose(), x2, y2, z2).setColor(r, g, b, a)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    public void drawLine(PoseStack poseStack, Vec3 start, Vec3 end, float r, float g, float b,
            float a) {
        drawLine(poseStack, (float) start.x, (float) start.y, (float) start.z, (float) end.x,
                (float) end.y, (float) end.z, r, g, b, a);
    }

    public void drawLine(PoseStack poseStack, float x1, float y1, float z1, float x2, float y2,
            float z2, float[] color, float alpha) {
        drawLine(poseStack, x1, y1, z1, x2, y2, z2, color[0], color[1], color[2], alpha);
    }

    public void drawLine(PoseStack poseStack, Vec3 start, Vec3 end, float[] color, float alpha) {
        drawLine(poseStack, start, end, color[0], color[1], color[2], alpha);
    }

    private Vec3[] getBoxCorners(Vec3 min, Vec3 max) {
        return new Vec3[]{new Vec3(min.x, min.y, min.z), new Vec3(max.x, min.y, min.z),
                new Vec3(max.x, min.y, max.z), new Vec3(min.x, min.y, max.z),
                new Vec3(min.x, max.y, min.z), new Vec3(max.x, max.y, min.z),
                new Vec3(max.x, max.y, max.z), new Vec3(min.x, max.y, max.z)};
    }

    private Vec3[] getBoxNormals() {
        return new Vec3[]{new Vec3(0, -1, 0), new Vec3(0, 1, 0), new Vec3(0, 0, -1),
                new Vec3(0, 0, 1), new Vec3(-1, 0, 0), new Vec3(1, 0, 0)};
    }

    private Vec3[] getBoxCorners(AABB aabb) {
        return getBoxCorners(new Vec3(aabb.minX, aabb.minY, aabb.minZ),
                new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ));
    }

    private int[] getBoxFaceCorners(int faceIndex) {
        switch (faceIndex) {
            case 0:
                return new int[]{0, 1, 2, 3};
            case 1:
                return new int[]{4, 5, 6, 7};
            case 2:
                return new int[]{0, 1, 5, 4};
            case 3:
                return new int[]{2, 3, 7, 6};
            case 4:
                return new int[]{0, 3, 7, 4};
            case 5:
                return new int[]{1, 2, 6, 5};
            default:
                return new int[]{};
        }
    }

    public void drawBox(PoseStack poseStack, Vec3[] corners, float r, float g, float b, float a) {
        drawLine(poseStack, corners[0], corners[1], r, g, b, a);
        drawLine(poseStack, corners[1], corners[2], r, g, b, a);
        drawLine(poseStack, corners[2], corners[3], r, g, b, a);
        drawLine(poseStack, corners[3], corners[0], r, g, b, a);

        drawLine(poseStack, corners[4], corners[5], r, g, b, a);
        drawLine(poseStack, corners[5], corners[6], r, g, b, a);
        drawLine(poseStack, corners[6], corners[7], r, g, b, a);
        drawLine(poseStack, corners[7], corners[4], r, g, b, a);

        drawLine(poseStack, corners[0], corners[4], r, g, b, a);
        drawLine(poseStack, corners[1], corners[5], r, g, b, a);
        drawLine(poseStack, corners[2], corners[6], r, g, b, a);
        drawLine(poseStack, corners[3], corners[7], r, g, b, a);
    }

    public void drawBox(PoseStack poseStack, Vec3[] corners, float[] color, float alpha) {
        drawBox(poseStack, corners, color[0], color[1], color[2], alpha);
    }

    public void drawBox(PoseStack poseStack, Vec3 min, Vec3 max, float r, float g, float b,
            float a) {
        Vec3[] vertices = getBoxCorners(min, max);
        drawBox(poseStack, vertices, r, g, b, a);
    }

    public void drawBox(PoseStack poseStack, Vec3 min, Vec3 max, float[] color, float alpha) {
        drawBox(poseStack, min, max, color[0], color[1], color[2], alpha);
    }

    public void drawBlockOutline(PoseStack poseStack, float[] color, float alpha) {
        drawBox(poseStack, getBoxCorners(new Vec3(0, 0, 0), new Vec3(1, 1, 1)), color, alpha);
    }

    public void drawAABB(PoseStack poseStack, AABB aabb, float[] color, float alpha) {
        Vec3[] vertices = getBoxCorners(aabb);
        drawBox(poseStack, vertices, color[0], color[1], color[2], alpha);
    }

    public void drawTriangle(PoseStack poseStack, Vec3 v1, Vec3 v2, Vec3 v3, float[] color1,
            float[] color2, float[] color3, float alpha1, float alpha2, float alpha3) {
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.debugQuads());
        vertexConsumer.addVertex(pose.pose(), (float) v1.x, (float) v1.y, (float) v1.z)
                .setColor(color1[0], color1[1], color1[2], alpha1);
        vertexConsumer.addVertex(pose.pose(), (float) v2.x, (float) v2.y, (float) v2.z)
                .setColor(color2[0], color2[1], color2[2], alpha2);
        vertexConsumer.addVertex(pose.pose(), (float) v3.x, (float) v3.y, (float) v3.z)
                .setColor(color3[0], color3[1], color3[2], alpha3);
        vertexConsumer.addVertex(pose.pose(), (float) v3.x, (float) v3.y, (float) v3.z)
                .setColor(color3[0], color3[1], color3[2], alpha3);
    }

    public void drawAABBWithProjection(PoseStack poseStack, AABB aabb, float[] color, float alpha,
            Vec3 projectionOriginPos, Vec3 playerLookDir, Vec3 cameraUpDir) {
        // TODO: Unfinished
        // Draw the AABB
        Vec3[] vertices = getBoxCorners(aabb);
        Vec3[] normals = getBoxNormals();
        drawBox(poseStack, vertices, color, alpha);
        // Determine what vertices are visible
        double[] dotProducts = new double[normals.length];
        for (int i = 0; i < normals.length; i++) {
            dotProducts[i] = normals[i].dot(playerLookDir);
        }
        Vec3 localX = playerLookDir.cross(cameraUpDir).normalize();
        Vec3 localY = localX.cross(playerLookDir).normalize();
        projectionOriginPos = projectionOriginPos.add(playerLookDir.scale(0.5));
        projectionOriginPos = projectionOriginPos.add(localX.scale(0.20));
        projectionOriginPos = projectionOriginPos.add(localY.scale(-0.20));
        for (int i = 0; i < dotProducts.length; i++) {
            if (dotProducts[i] < 0) {
                int[] faceCorners = getBoxFaceCorners(i);
                for (int j = 0; j < faceCorners.length; j++) {
                    drawLine(poseStack, vertices[faceCorners[j]], projectionOriginPos, color,
                            alpha);
                }
                // drawLine(poseStack, vertices[i], projectionOriginPos, color, alpha);
            }
            // if (i < vertices.length - 1) {
            // drawTriangle(poseStack, projectionOriginPos, vertices[i], vertices[i + 1], color,
            // color, color, alpha / 2, 0.0F, 0.0F);
            // }
        }
        // drawTriangle(poseStack, projectionOriginPos, vertices[0], vertices[1], color, color,
        // color,
        // alpha / 2, 0.0F, 0.0F);
    }
}
