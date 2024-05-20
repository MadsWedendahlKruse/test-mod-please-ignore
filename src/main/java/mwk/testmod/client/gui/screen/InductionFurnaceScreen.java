package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.CrafterMachineScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.common.block.inventory.InductionFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class InductionFurnaceScreen extends CrafterMachineScreen<InductionFurnaceMenu> {

    public InductionFurnaceScreen(InductionFurnaceMenu menu, Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title, GuiConfigs.INDUCTION_FURNACE);
    }
}
