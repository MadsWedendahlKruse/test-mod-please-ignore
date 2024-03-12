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

    public EnergyScreen(T menu, Inventory playerInventory, Component title,
            EnergyScreenPreset preset) {
        this(menu, playerInventory, title, preset.energyBarX, preset.energyBarY);
    }

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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        energyBar.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public static class EnergyScreenPreset {

        public final int energyBarX;
        public final int energyBarY;

        public EnergyScreenPreset(int energyBarX, int energyBarY) {
            this.energyBarX = energyBarX;
            this.energyBarY = energyBarY;
        }
    }
}
