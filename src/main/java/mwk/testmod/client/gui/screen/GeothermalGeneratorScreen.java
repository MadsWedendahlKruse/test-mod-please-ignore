package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.GeneratorScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.common.block.inventory.GeothermalGeneratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GeothermalGeneratorScreen extends GeneratorScreen<GeothermalGeneratorMenu> {

    public GeothermalGeneratorScreen(GeothermalGeneratorMenu menu, Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title, GuiConfigs.GEOTHERMAL_GENERATOR);
    }

}
