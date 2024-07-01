package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.CrafterScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.common.block.inventory.CrusherMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrusherScreen extends CrafterScreen<CrusherMenu> {

    public CrusherScreen(CrusherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, GuiConfigs.CRUSHER);
    }
}
