package mwk.testmod.client.gui.screen;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.screen.base.CrafterMachineScreen;
import mwk.testmod.client.gui.widgets.ProgressArrow3To1;
import mwk.testmod.common.block.inventory.SeparatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SeparatorScreen extends CrafterMachineScreen<SeparatorMenu> {

    private ProgressArrow3To1 progressArrow;

    public SeparatorScreen(SeparatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title,
                new ResourceLocation(TestMod.MODID, "textures/gui/container/separator.png"), 7, 27,
                176, 193, "separator", 81, 46);
    }

    @Override
    protected void init() {
        super.init();
        progressArrow = new ProgressArrow3To1(menu, this.leftPos + 70, this.topPos + 29);
    }

    @Override
    protected void renderProgress(GuiGraphics guiGraphics) {
        progressArrow.render(guiGraphics);
    }

}