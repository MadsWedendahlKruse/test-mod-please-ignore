package mwk.testmod.client.gui.widgets;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ProgresArrow extends ProgressSprite {

    public static final WidgetSprites PROGRESS_ARROW_18_SPRITES =
            new WidgetSprites(new ResourceLocation(TestMod.MODID, "container/progress/arrow_18"),
                    new ResourceLocation(TestMod.MODID, "container/progress/arrow_18_background"));

    public static final int WIDTH = 18;
    public static final int HEIGHT = 10;
    public static final int BACKGROUND_OFFSET_X = 0;
    public static final int BACKGROUND_OFFSET_Y = 1;

    public ProgresArrow(CrafterMachineMenu menu, int x, int y) {
        this(menu, x, y, WIDTH, HEIGHT);
    }

    public ProgresArrow(CrafterMachineMenu menu, int x, int y, int width, int height) {
        super(getArrowSprites(width), menu, x, y, width, height, true, BACKGROUND_OFFSET_X,
                BACKGROUND_OFFSET_Y);
    }

    private static WidgetSprites getArrowSprites(int width) {
        return switch (width) {
            case 18 -> PROGRESS_ARROW_18_SPRITES;
            default -> throw new IllegalArgumentException("Invalid width: " + width);
        };
    }
}
