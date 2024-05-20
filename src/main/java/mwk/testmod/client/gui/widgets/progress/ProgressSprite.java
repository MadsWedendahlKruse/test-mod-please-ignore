package mwk.testmod.client.gui.widgets.progress;

import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

public class ProgressSprite {

    public static final int MAX_PROGRESS_REDUCTION = 3;

    private final WidgetSprites sprites;
    private final CrafterMachineMenu menu;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final boolean horizontal;
    private final int backgroundOffsetX;
    private final int backgroundOffsetY;

    public ProgressSprite(WidgetSprites sprites, CrafterMachineMenu menu, int x, int y, int width,
            int height, boolean horizontal, int backgroundOffsetX, int backgroundOffsetY) {
        this.sprites = sprites;
        this.menu = menu;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.horizontal = horizontal;
        this.backgroundOffsetX = backgroundOffsetX;
        this.backgroundOffsetY = backgroundOffsetY;
    }

    public void render(GuiGraphics guiGraphics, int progress, int maxProgress) {
        ResourceLocation background = sprites.get(true, true);
        if (background != null) {
            guiGraphics.blitSprite(background, width, height, 0, 0, x + backgroundOffsetX,
                    y + backgroundOffsetY, width, height);
        }
        if (progress > 0 && maxProgress > 0) {
            // We don't want the sprite to disappear immediately when the progress is 100%,
            // so max progress is reduced slightly.
            float progressFraction =
                    (float) progress / (float) (maxProgress - MAX_PROGRESS_REDUCTION);
            int maxSize = horizontal ? width : height;
            int progressSize = (int) (Math.min(1.0f, progressFraction) * maxSize);
            ResourceLocation sprite = sprites.get(true, false);
            if (horizontal) {
                // Only clip the width of the sprite for horizontal progress bars.
                guiGraphics.blitSprite(sprite, width, height, 0, 0, x, y, progressSize, height);
            } else {
                // Only clip the height of the sprite for vertical progress bars.
                // Notice how the 'y' position of the sprite is adjusted to start drawing from the
                // bottom.
                int yOffset = height - progressSize; // Start drawing from the bottom
                guiGraphics.blitSprite(sprite, width, height, 0, yOffset, x, y + yOffset, width,
                        progressSize);
            }
        }
    }

    public void render(GuiGraphics guiGraphics) {
        render(guiGraphics, menu.getProgress(), menu.getMaxProgress());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rect2i getBounds() {
        return new Rect2i(x, y, width, height);
    }
}
