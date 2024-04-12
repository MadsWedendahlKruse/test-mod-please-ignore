package mwk.testmod.client.gui.screen;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.screen.base.ParallelCrafterMachineScreen;
import mwk.testmod.common.block.inventory.CrusherMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrusherScreen extends ParallelCrafterMachineScreen<CrusherMenu> {

    public static final int TEXTURE_WIDTH = 188;
    public static final int TEXTURE_HEIGHT = 193;

    public CrusherScreen(CrusherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title,
                new ResourceLocation(TestMod.MODID, "textures/gui/container/3x3_parallel.png"), 7,
                27, 188, 193, "dust", 95, 46, 2, 93, 27, 40);
    }
}
