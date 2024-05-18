package mwk.testmod.client.gui.widgets.progress;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ProgressArrowSingle extends ProgressArrow {

    public static final WidgetSprites PROGRESS_ARROW_18_SPRITES =
            new WidgetSprites(new ResourceLocation(TestMod.MODID, "container/progress/arrow_18"),
                    new ResourceLocation(TestMod.MODID, "container/progress/arrow_18_background"));

    public static final int WIDTH = 18;
    public static final int HEIGHT = 10;

    public ProgressArrowSingle(CrafterMachineMenu menu, int x, int y) {
        super(PROGRESS_ARROW_18_SPRITES, menu, x, y, WIDTH, HEIGHT);
    }

}
