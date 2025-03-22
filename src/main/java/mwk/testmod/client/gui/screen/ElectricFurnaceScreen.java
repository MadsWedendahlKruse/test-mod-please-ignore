package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.CrafterScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.common.block.inventory.ElectricFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ElectricFurnaceScreen extends CrafterScreen<ElectricFurnaceMenu> {

    public ElectricFurnaceScreen(ElectricFurnaceMenu menu, Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title, GuiConfigs.ELECTRIC_FURNACE);
    }
}
