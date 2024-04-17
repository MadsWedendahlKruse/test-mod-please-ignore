package mwk.testmod.client.render.hologram.components;

import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.common.util.ColorUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;

public class HologramTextRenderer {

    private static final float HOLOGRAM_TEXT_SCALE = 0.01F;
    private static final float HOLOGRAM_TEXT_Z_OFFSET = -0.1F;
    // Text is for some reason rendered upside down by default
    private static final Quaternionf HOLOGRAM_TEXT_ROTATION =
            new Quaternionf().rotationZ((float) Math.PI);


    private Font font;
    private BufferSource bufferSource;

    public HologramTextRenderer(Font font, BufferSource bufferSource) {
        this.font = font;
        this.bufferSource = bufferSource;
    }

    public void renderText(PoseStack poseStack, Component component, float x, float y, float z,
            int textColor) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(HOLOGRAM_TEXT_SCALE, HOLOGRAM_TEXT_SCALE, HOLOGRAM_TEXT_SCALE);
        poseStack.mulPose(HOLOGRAM_TEXT_ROTATION);
        font.drawInBatch(component, 0, 0, textColor, false, poseStack.last().pose(), bufferSource,
                DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    public void renderText(PoseStack poseStack, Component component, float x, float y, float z) {
        renderText(poseStack, component, x, y, z, ColorUtils.TEXT_WHITE);
    }

    public void renderText(PoseStack poseStack, Component component, float x, float y) {
        renderText(poseStack, component, x, y, HOLOGRAM_TEXT_Z_OFFSET, ColorUtils.TEXT_WHITE);
    }

    public void renderTextList(PoseStack poseStack, Component[] components, float x, float y,
            float z, float spacing) {
        for (int i = 0; i < components.length; i++) {
            renderText(poseStack, components[i], x, y - (i * spacing), z);
        }
    }
}
