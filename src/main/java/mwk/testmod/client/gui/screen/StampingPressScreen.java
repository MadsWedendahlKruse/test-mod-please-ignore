package mwk.testmod.client.gui.screen;

import mwk.testmod.client.gui.screen.base.CrafterScreen;
import mwk.testmod.client.gui.screen.config.GuiConfigs;
import mwk.testmod.client.gui.widgets.MissingStampingDieIcon;
import mwk.testmod.common.block.inventory.StampingPressMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class StampingPressScreen extends CrafterScreen<StampingPressMenu> {

    private MissingStampingDieIcon missingDieIcon;

    public StampingPressScreen(StampingPressMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, GuiConfigs.STAMPING_PRESS);
    }

    @Override
    protected void init() {
        super.init();
        this.missingDieIcon = new MissingStampingDieIcon(menu, this.leftPos + 44, this.topPos + 27);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        this.missingDieIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
