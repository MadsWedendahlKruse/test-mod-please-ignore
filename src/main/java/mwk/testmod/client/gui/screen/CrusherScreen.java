package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.ParallelCrafterMachineScreen;
import mwk.testmod.common.block.inventory.CrusherMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrusherScreen extends ParallelCrafterMachineScreen<CrusherMenu> {

    public static final int TEXTURE_WIDTH = 188;
    public static final int TEXTURE_HEIGHT = 193;

    public CrusherScreen(CrusherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, "dust",
                ParallelCrafterMachineScreen.PRESET_3X3_PARALLEL);
    }
}
