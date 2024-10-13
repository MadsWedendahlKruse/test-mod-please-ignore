package mwk.testmod.client.gui.widgets.progress;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ProgressIcon extends ProgressSprite {

    public static final int WIDTH = 13;
    public static final int HEIGHT = 13;
    public static final int BACKGROUND_OFFSET_X = 1;
    public static final int BACKGROUND_OFFSET_Y = 1;

    public ProgressIcon(WidgetSprites sprites, ProcessingMenu menu, int x, int y) {
        this(sprites, menu, x, y, WIDTH, HEIGHT, false);
    }

    public ProgressIcon(WidgetSprites sprites, ProcessingMenu menu, int x, int y, int width,
            int height, boolean horizontal) {
        super(sprites, menu, x, y, width, height, horizontal, BACKGROUND_OFFSET_X,
                BACKGROUND_OFFSET_Y);
    }

    public static WidgetSprites createSprites(String iconName) {
        return new WidgetSprites(
                ResourceLocation.fromNamespaceAndPath(TestMod.MODID,
                        "container/progress/" + iconName),
                ResourceLocation.fromNamespaceAndPath(TestMod.MODID,
                        "container/progress/" + iconName + "_background"));
    }
}
