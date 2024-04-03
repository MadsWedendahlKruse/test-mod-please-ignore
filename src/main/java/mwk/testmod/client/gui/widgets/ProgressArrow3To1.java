package mwk.testmod.client.gui.widgets;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class ProgressArrow3To1 extends ProgressArrow {

    public static final WidgetSprites PROGRESS_ARROW_3_TO_1_SPRITES = new WidgetSprites(
            new ResourceLocation(TestMod.MODID, "container/progress/arrow_3_to_1"),
            new ResourceLocation(TestMod.MODID, "container/progress/arrow_3_to_1_background"));

    public ProgressArrow3To1(CrafterMachineMenu menu, int x, int y) {
        super(PROGRESS_ARROW_3_TO_1_SPRITES, menu, x, y, 36, 46);
    }

}
