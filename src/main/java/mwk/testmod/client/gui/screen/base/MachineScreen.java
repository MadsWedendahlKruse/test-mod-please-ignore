package mwk.testmod.client.gui.screen.base;

import java.util.ArrayList;
import java.util.Collection;
import mwk.testmod.client.gui.widgets.EnergyBar;
import mwk.testmod.client.gui.widgets.FluidBar;
import mwk.testmod.client.gui.widgets.TemporalFluxBar;
import mwk.testmod.client.gui.widgets.panels.base.MachinePanel;
import mwk.testmod.client.gui.widgets.panels.base.PanelManager;
import mwk.testmod.client.gui.widgets.panels.base.PanelSide;
import mwk.testmod.client.gui.widgets.resource.ResourceBar;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.entity.modules.FluidTankModule;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class MachineScreen<T extends MachineMenu> extends AbstractContainerScreen<T> {

    public static final int INVENTORY_LABEL_Y_OFFSET = 11;
    public static final int INVETORY_PADDING_X = 8;
    public static final int INVETORY_PADDING_Y = 16;

    protected ResourceLocation texture;
    protected T menu;
    protected PanelManager panelManager;
    private final int resourceBarX;
    private final int resourceBarY;
    private ArrayList<ResourceBar> resourceBars;
    private FluidBar[] fluidBars;

    public MachineScreen(T menu, Inventory playerInventory, Component title,
            ResourceLocation texture, int resourceBarX, int resourceBarY, int imageWidth,
            int imageHeight) {
        super(menu, playerInventory, title);
        this.menu = menu;
        this.texture = texture;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.inventoryLabelX = menu.playerInventoryX;
        this.inventoryLabelY = menu.playerInventoryY - INVENTORY_LABEL_Y_OFFSET;
        this.resourceBarX = resourceBarX;
        this.resourceBarY = resourceBarY;
    }

    protected void addMachinePanel(MachinePanel panel) {
        panelManager.addPanel(panel);
    }

    protected void addMachinePanel(MachinePanel panel, PanelSide side) {
        panelManager.addPanel(panel, side);
    }

    protected abstract void addMachinePanels();

    @Override
    protected void init() {
        super.init();
        panelManager = new PanelManager(this.leftPos, this.topPos, this.imageWidth,
                this.imageHeight, 0, 10);
        addMachinePanels();
        MachineBlockEntity machine = menu.getBlockEntity();
        resourceBars = new ArrayList<>();
        if (machine.energy().isPresent()) {
            resourceBars.add(
                    new EnergyBar(menu, this.leftPos + resourceBarX, this.topPos + resourceBarY));
        }
        if (machine.temporalFlux().isPresent()) {
            resourceBars.add(new TemporalFluxBar(menu, this.leftPos + resourceBarX,
                    this.topPos + resourceBarY));
        }
        if (machine.fluidTanks().isPresent()) {
            FluidTankModule fluidTankModule = machine.fluidTanks().get();
            final int inputTanks = fluidTankModule.getInputTanks();
            final int outputTanks = fluidTankModule.getOutputTanks();
            fluidBars = new FluidBar[inputTanks + outputTanks];
            for (int i = 0; i < inputTanks; i++) {
                fluidBars[i] = new FluidBar(fluidTankModule.getInputFluidHandler(null), i,
                        this.leftPos + 35, this.topPos + 27);
            }
            for (int i = 0; i < outputTanks; i++) {
                int tankIdx = i + inputTanks;
                fluidBars[tankIdx] = new FluidBar(fluidTankModule.getOutputFluidHandler(null),
                        tankIdx, this.leftPos + 35, this.topPos + 27);
            }
        } else {
            fluidBars = new FluidBar[0];
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight);
        panelManager.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        for (ResourceBar resourceBar : resourceBars) {
            resourceBar.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        for (FluidBar fluidBar : fluidBars) {
            fluidBar.render(guiGraphics, mouseX, mouseY, partialTick);
        }
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
        // If a slot if hovered, don't allow the panel to be clicked
        if (hoveredSlot == null) {
            panelManager.mouseClicked(pMouseX, pMouseY, pButton);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop,
            int pMouseButton) {
        if (panelManager.isMouseOver(pMouseX, pMouseY)) {
            return false;
        }
        return super.hasClickedOutside(pMouseX, pMouseY, pGuiLeft, pGuiTop, pMouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        panelManager.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX,
            double dragY) {
        panelManager.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        panelManager.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        panelManager.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * @return A collection of areas that JEI should not render over.
     */
    public Collection<Rect2i> getGuiExtraAreas() {
        return panelManager.getGuiExtraAreas();
    }
}
