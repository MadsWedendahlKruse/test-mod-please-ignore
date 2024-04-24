package mwk.testmod.client.gui.screen.base;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.ProgressIcon;
import mwk.testmod.client.gui.widgets.panels.EnergyPanel;
import mwk.testmod.client.gui.widgets.panels.PanelManager;
import mwk.testmod.client.gui.widgets.panels.UpgradePanel;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class CrafterMachineScreen<T extends CrafterMachineMenu>
        extends BaseMachineScreen<T> {

    private ProgressIcon progressIcon;
    private final WidgetSprites progressIconSprites;
    private final int progressIconX;
    private final int progressIconY;

    public CrafterMachineScreen(T menu, Inventory playerInventory, Component title,
            ResourceLocation texture, int energyBarX, int energyBarY, int imageWidth,
            int imageHeight, String iconName, int progressIconX, int progressIconY) {
        super(menu, playerInventory, title, texture, energyBarX, energyBarY, imageWidth,
                imageHeight);
        this.progressIconSprites = ProgressIcon.createSprites(iconName);
        this.progressIconX = progressIconX;
        this.progressIconY = progressIconY;
    }

    protected abstract void renderProgress(GuiGraphics guiGraphics);

    @Override
    protected void addMachinePanels() {
        panelManager.addPanel(new EnergyPanel(menu), PanelManager.Side.LEFT);
        int upgradeRows = (int) Math.ceil((float) menu.upgradeSlots / 3);
        int upgradeColumns = (int) Math.ceil((float) menu.upgradeSlots / upgradeRows);
        panelManager.addPanel(new UpgradePanel(menu, upgradeRows, upgradeColumns),
                PanelManager.Side.RIGHT);
    }

    @Override
    protected void init() {
        super.init();
        progressIcon = new ProgressIcon(progressIconSprites, menu, this.leftPos + progressIconX,
                this.topPos + progressIconY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        progressIcon.render(guiGraphics);
        renderProgress(guiGraphics);
    }
}
