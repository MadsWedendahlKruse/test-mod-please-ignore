package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.ParallelCrafterMachineScreen;
import mwk.testmod.common.block.inventory.SuperFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SuperFurnaceScreen extends ParallelCrafterMachineScreen<SuperFurnaceMenu> {

    public static final int TEXTURE_WIDTH = 188;
    public static final int TEXTURE_HEIGHT = 193;

    public SuperFurnaceScreen(SuperFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, "fire",
                ParallelCrafterMachineScreen.PRESET_3X3_PARALLEL);
    }
}
