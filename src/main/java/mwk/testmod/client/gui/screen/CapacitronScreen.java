package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.MachineScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.client.gui.widgets.panels.InfoPanel;
import mwk.testmod.common.block.inventory.CapacitronMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CapacitronScreen extends MachineScreen<CapacitronMenu> {

    public CapacitronScreen(CapacitronMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, GuiConfigs.CAPACITRON.background(),
                GuiConfigs.CAPACITRON.energyBarX(), GuiConfigs.CAPACITRON.energyBarY(),
                GuiConfigs.CAPACITRON.imageWidth(), GuiConfigs.CAPACITRON.imageHeight());
    }

    @Override
    protected void addMachinePanels() {
        addMachinePanel(new InfoPanel(menu.getBlockEntity()));
    }
}
