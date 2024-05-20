package mwk.testmod.client.gui.widgets.progress;

import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;

public class ProgressArrowFactory {

    public enum Type {
        SINGLE, ONE_TO_THREE
    }

    public static ProgressArrow create(Type type, CrafterMachineMenu menu, int x, int y) {
        return switch (type) {
            case SINGLE -> new ProgressArrowSingle(menu, x, y);
            case ONE_TO_THREE -> new ProgressArrow1To3(menu, x, y);
            default -> null;
        };
    }
}
