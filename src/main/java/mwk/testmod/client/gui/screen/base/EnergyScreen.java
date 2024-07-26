package mwk.testmod.client.gui.screen.base;

import mwk.testmod.client.gui.widgets.EnergyBar;
import mwk.testmod.common.block.inventory.base.EnergyMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EnergyScreen<T extends EnergyMenu> extends AbstractContainerScreen<T> {

    private EnergyBar energyBar;
    private int energyBarX;
    private int energyBarY;

    public EnergyScreen(T menu, Inventory playerInventory, Component title, int energyBarX,
            int energyBarY) {
        super(menu, playerInventory, title);
        this.energyBarX = energyBarX;
        this.energyBarY = energyBarY;
    }

    @Override
    protected void init() {
        super.init();
        energyBar = new EnergyBar(menu, this.leftPos + energyBarX, this.topPos + energyBarY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        energyBar.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
