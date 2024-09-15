package mwk.testmod.client.gui.widgets.progress;

import mwk.testmod.common.block.inventory.base.ProcessingMenu;

public class ProgressArrowFactory {

    public enum ArrowType {
        SINGLE, ONE_TO_ONE, ONE_TO_THREE
    }

    public static ProgressArrow create(ArrowType arrowType, ProcessingMenu menu, int x, int y) {
        return switch (arrowType) {
            case SINGLE -> new ProgressArrowSingle(menu, x, y);
            case ONE_TO_ONE -> new ProgressArrow1To1(menu, x, y);
            case ONE_TO_THREE -> new ProgressArrow1To3(menu, x, y);
            default -> null;
        };
    }
}
