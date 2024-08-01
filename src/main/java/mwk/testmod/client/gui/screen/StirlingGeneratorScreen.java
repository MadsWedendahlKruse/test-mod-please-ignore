package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.GeneratorScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.common.block.inventory.StirlingGeneratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class StirlingGeneratorScreen extends GeneratorScreen<StirlingGeneratorMenu> {

    public StirlingGeneratorScreen(StirlingGeneratorMenu menu, Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title, GuiConfigs.REDSTONE_GENERATOR);
    }

}
