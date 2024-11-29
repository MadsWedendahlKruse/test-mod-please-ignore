package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.GeneratorScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.common.block.inventory.TemporalSieveMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TemporalSieveScreen extends GeneratorScreen<TemporalSieveMenu> {

    public TemporalSieveScreen(TemporalSieveMenu menu, Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title, GuiConfigs.REDSTONE_GENERATOR);
    }

}
