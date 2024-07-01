package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.CrafterScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.common.block.inventory.SeparatorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SeparatorScreen extends CrafterScreen<SeparatorMenu> {

    public SeparatorScreen(SeparatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, GuiConfigs.SEPARATOR);
    }
}
