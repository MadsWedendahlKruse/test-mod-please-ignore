package mwk.testmod.client.gui.screen.base;

import mwk.testmod.client.gui.screen.config.GuiConfig;
import mwk.testmod.client.gui.widgets.panels.EnergyPanel;
import mwk.testmod.client.gui.widgets.panels.base.PanelSide;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrafterScreen<T extends ProcessingMenu> extends ProcessingScreen<T> {

    public CrafterScreen(T menu, Inventory playerInventory, Component title, GuiConfig config) {
        super(menu, playerInventory, title, config);
    }

    @Override
    protected void addMachinePanels() {
        super.addMachinePanels();
        addMachinePanel(new EnergyPanel(menu), PanelSide.LEFT);
    }

}
