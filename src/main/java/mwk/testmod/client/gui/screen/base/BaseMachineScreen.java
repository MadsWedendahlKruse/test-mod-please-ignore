package mwk.testmod.client.gui.screen.base;

import mwk.testmod.client.gui.widgets.panels.EnergyPanel;
import mwk.testmod.client.gui.widgets.panels.PanelManager;
import mwk.testmod.common.block.inventory.base.BaseMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BaseMachineScreen<T extends BaseMachineMenu> extends EnergyScreen<T> {

    public static final int TEXTURE_INVENTORY_WIDTH = 176;
    public static final int TEXTURE_INVENTORY_HEIGHT = 98;
    public static final int INVENTORY_LABEL_Y_OFFSET = 11;
    public static final int INVETORY_PADDING_X = 8;
    public static final int INVETORY_PADDING_Y = 16;

    protected ResourceLocation texture;
    protected T menu;
    protected PanelManager panelManager;

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title,
            BaseMachineScreenPreset preset) {
        this(menu, playerInventory, title, preset.texture, preset.energyBarX, preset.energyBarY,
                preset.imageWidth, preset.imageHeight);
    }

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title,
            ResourceLocation texture, int energyBarX, int energyBarY, int imageWidth,
            int imageHeight) {
        super(menu, playerInventory, title, energyBarX, energyBarY);
        this.menu = menu;
        this.texture = texture;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.inventoryLabelX = menu.getPlayerInventoryX();
        this.inventoryLabelY = menu.getPlayerInventoryY() - INVENTORY_LABEL_Y_OFFSET;
    }

    protected void addMachinePanels() {
        panelManager.addPanel(new EnergyPanel());
        panelManager.addPanel(new EnergyPanel());
        panelManager.addPanel(new EnergyPanel());
        panelManager.addPanel(new EnergyPanel());
        panelManager.addPanel(new EnergyPanel());
        panelManager.addPanel(new EnergyPanel());
    }

    @Override
    protected void init() {
        super.init();
        panelManager =
                new PanelManager(this.leftPos, this.topPos, this.imageWidth, this.height, 0, 10);
        addMachinePanels();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight);
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        panelManager.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawCenteredString(this.font, this.title, this.imageWidth / 2,
                this.titleLabelY, 0xffffff);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX,
                this.inventoryLabelY, 4210752, false);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        panelManager.mouseClicked(pMouseX, pMouseY, pButton);
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public static class BaseMachineScreenPreset extends EnergyScreenPreset {

        public final ResourceLocation texture;
        public final int imageWidth;
        public final int imageHeight;

        public BaseMachineScreenPreset(int energyBarX, int energyBarY, ResourceLocation texture,
                int imageWidth, int imageHeight) {
            super(energyBarX, energyBarY);
            this.texture = texture;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
        }
    }
}
