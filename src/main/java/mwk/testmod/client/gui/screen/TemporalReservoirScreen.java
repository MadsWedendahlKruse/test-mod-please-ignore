package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.MachineScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.client.gui.widgets.panels.InfoPanel;
import mwk.testmod.common.block.inventory.TemporalReservoirMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TemporalReservoirScreen extends MachineScreen<TemporalReservoirMenu> {

    public TemporalReservoirScreen(
            TemporalReservoirMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, GuiConfigs.CHRONO_CELL.background(),
                GuiConfigs.CHRONO_CELL.energyBarX(), GuiConfigs.CHRONO_CELL.energyBarY(),
                GuiConfigs.CHRONO_CELL.imageWidth(), GuiConfigs.CHRONO_CELL.imageHeight());
    }

    @Override
    protected void addMachinePanels() {
        addMachinePanel(new InfoPanel(menu.getBlockEntity()));
    }
}
