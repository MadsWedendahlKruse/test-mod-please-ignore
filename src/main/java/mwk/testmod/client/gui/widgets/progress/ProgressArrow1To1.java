package mwk.testmod.client.gui.widgets.progress;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ProgressArrow1To1 extends ProgressArrow {

    public static final WidgetSprites PROGRESS_ARROW_1_TO_3_SPRITES = new WidgetSprites(
            new ResourceLocation(TestMod.MODID, "container/progress/arrow_1_to_1"),
            new ResourceLocation(TestMod.MODID, "container/progress/arrow_1_to_1_background"));

    public static final int WIDTH = 36;
    public static final int HEIGHT = 10;

    public ProgressArrow1To1(ProcessingMenu menu, int x, int y) {
        super(PROGRESS_ARROW_1_TO_3_SPRITES, menu, x, y, WIDTH, HEIGHT);
    }

}
