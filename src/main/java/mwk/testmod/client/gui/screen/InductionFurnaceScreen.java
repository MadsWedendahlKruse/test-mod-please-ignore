package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.ParallelCrafterMachineScreen;
import mwk.testmod.common.block.inventory.InductionFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class InductionFurnaceScreen extends ParallelCrafterMachineScreen<InductionFurnaceMenu> {

    public static final int TEXTURE_WIDTH = 188;
    public static final int TEXTURE_HEIGHT = 193;

    public InductionFurnaceScreen(InductionFurnaceMenu menu, Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title, "fire",
                ParallelCrafterMachineScreen.PRESET_3X3_PARALLEL);
    }
}
