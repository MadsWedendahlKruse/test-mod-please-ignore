package mwk.testmod.client.gui;

import com.ibm.icu.text.NumberFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Locale;
import mwk.testmod.TestMod;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class GuiUtils {

    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    public static class GuiTextElement {

        public final Component title;
        public final Component text;

        public GuiTextElement(String titleKey, String textKey, Object... args) {
            this.title = Component.translatable(titleKey);
            this.text = Component.translatable(textKey, args);
        }
    }

    public static void renderTextElements(GuiGraphics guiGraphics, Font font,
            GuiTextElement[] elements, int x, int y, int titleColor, int textColor,
            int lineHeight) {
        for (int i = 0; i < elements.length; i++) {
            GuiTextElement element = elements[i];
            guiGraphics.drawString(font, element.title, x, y + (2 * i) * lineHeight, titleColor,
                    true);
            guiGraphics.drawString(font, element.text, x, y + (2 * i + 1) * lineHeight, textColor,
                    false);
        }
    }

    public static final ResourceLocation ITEM_SLOT_SPRITE =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/item_slot");
    public static final int ITEM_SLOT_SIZE = 18;

    /**
     * Renders an item slot sprite at the given position. The position is the top-left corner of the
     * item slot, meaning the border sprite will be rendered 1 pixel to the left and 1 pixel above
     * the given position. The width and height can be used to render e.g. the border of an energy
     * bar
     *
     * @param guiGraphics the graphics object to render with
     * @param x           the x position to render the item slot at
     * @param y           the y position to render the item slot at
     * @param width       the width of the item slot
     * @param height      the height of the item slot
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

    public static void renderSprite(GuiGraphics guiGraphics, TextureAtlasSprite sprite, int x,
            int y, int width, int height, int color) {
        // This is basically copy-pasted from GuiGraphics#innerBlit which is not public
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance()
                .begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex(matrix4f, x, y + height, 0).setUv(sprite.getU0(), sprite.getV1())
                .setColor(color);
        bufferBuilder.addVertex(matrix4f, x + width, y + height, 0)
                .setUv(sprite.getU1(), sprite.getV1())
                .setColor(color);
        bufferBuilder.addVertex(matrix4f, x + width, y, 0).setUv(sprite.getU1(), sprite.getV0())
                .setColor(color);
        bufferBuilder.addVertex(matrix4f, x, y, 0).setUv(sprite.getU0(), sprite.getV0())
                .setColor(color);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    /**
     * Represents which direction our tiling is done when extending past the max size. Shamelessly
     * copied from Mekanism
     */
    public enum TilingDirection {
        /**
         * Textures are being tiled/filled from top left to bottom right.
         */
        DOWN_RIGHT(true, true),
        /**
         * Textures are being tiled/filled from top right to bottom left.
         */
        DOWN_LEFT(true, false),
        /**
         * Textures are being tiled/filled from bottom left to top right.
         */
        UP_RIGHT(false, true),
        /**
         * Textures are being tiled/filled from bottom right to top left.
         */
        UP_LEFT(false, false);

        private final boolean down;
        private final boolean right;

        TilingDirection(boolean down, boolean right) {
            this.down = down;
            this.right = right;
        }
    }

    // Shamelessly copied from Mekanism
    // https://github.com/mekanism/Mekanism/blob/release/1.20.4/src/main/java/mekanism/client/gui/GuiUtils.java#L92
    public static void drawTiledSprite(GuiGraphics guiGraphics, int xPosition, int yPosition,
            int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite,
            int textureWidth, int textureHeight, int zLevel, TilingDirection tilingDirection,
            boolean blend) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        int xTileCount = desiredWidth / textureWidth;
        int xRemainder = desiredWidth - (xTileCount * textureWidth);
        int yTileCount = desiredHeight / textureHeight;
        int yRemainder = desiredHeight - (yTileCount * textureHeight);
        int yStart = yPosition + yOffset;
        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;
        if (blend) {
            RenderSystem.enableBlend();
        }
        // Note: We still use the tesselator as that is what GuiGraphics#innerBlit does
        BufferBuilder vertexBuffer = Tesselator.getInstance()
                .begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : textureWidth;
            if (width == 0) {
                break;
            }
            int x = xPosition + (xTile * textureWidth);
            int maskRight = textureWidth - width;
            int shiftedX = x + textureWidth - maskRight;
            float uLocalDif = uDif * maskRight / textureWidth;
            float uLocalMin;
            float uLocalMax;
            if (tilingDirection.right) {
                uLocalMin = uMin;
                uLocalMax = uMax - uLocalDif;
            } else {
                uLocalMin = uMin + uLocalDif;
                uLocalMax = uMax;
            }
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int height = (yTile == yTileCount) ? yRemainder : textureHeight;
                if (height == 0) {
                    // Note: We don't want to fully break out because our height will be zero if we
                    // are looking to
                    // draw the remainder, but there is no remainder as it divided evenly
                    break;
                }
                int y = yStart - ((yTile + 1) * textureHeight);
                int maskTop = textureHeight - height;
                float vLocalDif = vDif * maskTop / textureHeight;
                float vLocalMin;
                float vLocalMax;
                if (tilingDirection.down) {
                    vLocalMin = vMin;
                    vLocalMax = vMax - vLocalDif;
                } else {
                    vLocalMin = vMin + vLocalDif;
                    vLocalMax = vMax;
                }
                vertexBuffer.addVertex(matrix4f, x, y + textureHeight, zLevel)
                        .setUv(uLocalMin, vLocalMax);
                vertexBuffer.addVertex(matrix4f, shiftedX, y + textureHeight, zLevel)
                        .setUv(uLocalMax, vLocalMax);
                vertexBuffer.addVertex(matrix4f, shiftedX, y + maskTop, zLevel)
                        .setUv(uLocalMax, vLocalMin);
                vertexBuffer.addVertex(matrix4f, x, y + maskTop, zLevel)
                        .setUv(uLocalMin, vLocalMin);
            }
        }
        BufferUploader.drawWithShader(vertexBuffer.buildOrThrow());
        if (blend) {
            RenderSystem.disableBlend();
        }
    }
}
