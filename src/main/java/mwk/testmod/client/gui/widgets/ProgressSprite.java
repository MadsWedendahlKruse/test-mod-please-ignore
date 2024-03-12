package mwk.testmod.client.gui.widgets;

import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ProgressSprite {

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

    public void render(GuiGraphics guiGraphics) {
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        ResourceLocation background = sprites.get(true, true);
        if (background != null) {
            guiGraphics.blitSprite(background, width, height, 0, 0, x + backgroundOffsetX,
                    y + backgroundOffsetY, width, height);
        }
        if (progress > 0 && maxProgress > 0) {
            int progressScaled =
                    (int) ((float) progress / (float) maxProgress * (horizontal ? width : height));
            ResourceLocation sprite = sprites.get(true, false);
            if (horizontal) {
                // Only clip the width of the sprite for horizontal progress bars.
                guiGraphics.blitSprite(sprite, width, height, 0, 0, x, y, progressScaled, height);
            } else {
                // Only clip the height of the sprite for vertical progress bars.
                // Notice how the 'y' position of the sprite is adjusted to start drawing from the
                // bottom.
                int yOffset = height - progressScaled; // Start drawing from the bottom
                guiGraphics.blitSprite(sprite, width, height, 0, yOffset, x, y + yOffset, width,
                        progressScaled);
            }
        }
    }
}
