package mwk.testmod.client.gui.widgets.progress;

import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.components.WidgetSprites;

public class ProgressArrow extends ProgressSprite {

    public static final int BACKGROUND_OFFSET_X = 0;
    public static final int BACKGROUND_OFFSET_Y = 1;

    public ProgressArrow(WidgetSprites arrowSprites, CrafterMachineMenu menu, int x, int y,
            int width, int height) {
        super(arrowSprites, menu, x, y, width, height, true, BACKGROUND_OFFSET_X,
                BACKGROUND_OFFSET_Y);
    }
}
