package mwk.testmod.client.gui.screen.base;

import java.util.ArrayList;
import mwk.testmod.client.gui.screen.config.GuiConfig;
import mwk.testmod.client.gui.widgets.panels.InfoPanel;
import mwk.testmod.client.gui.widgets.panels.SettingsPanel;
import mwk.testmod.client.gui.widgets.panels.UpgradePanel;
import mwk.testmod.client.gui.widgets.panels.base.PanelSide;
import mwk.testmod.client.gui.widgets.progress.ProgressArrowFactory;
import mwk.testmod.client.gui.widgets.progress.ProgressArrowFactory.ArrowType;
import mwk.testmod.client.gui.widgets.progress.ProgressIcon;
import mwk.testmod.client.gui.widgets.progress.ProgressSprite;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class ProcessingScreen<T extends ProcessingMenu> extends MachineScreen<T> {

    private final ArrayList<ProgressSprite> progressSprites;

    private final WidgetSprites progressIconSprites;
    private final int progressIconX;
    private final int progressIconY;
    private final ArrowType progressArrowType;
    private final int progressArrows;
    private final int progressArrowX;
    private final int progressArrowY;
    private final int progressArrowSpacing;

    public ProcessingScreen(T menu, Inventory playerInventory, Component title, GuiConfig config) {
        this(menu, playerInventory, title, config.background(), config.energyBarX(),
                config.energyBarY(), config.imageWidth(), config.imageHeight(),
                config.progressIconName(), config.progressIconX(), config.progressIconY(),
                config.progressArrowType(), config.progressArrows(), config.progressArrowX(),
                config.progressArrowY(), config.progressArrowSpacing());
    }

    public ProcessingScreen(T menu, Inventory playerInventory, Component title,
            ResourceLocation texture, int energyBarX, int energyBarY, int imageWidth,
            int imageHeight, String iconName, int progressIconX, int progressIconY,
            ArrowType progressArrowType, int progressArrows, int progressArrowX,
            int progressArrowY, int progressArrowSpacing) {
        super(menu, playerInventory, title, texture, energyBarX, energyBarY, imageWidth,
                imageHeight);
        this.progressSprites = new ArrayList<>();
        this.progressIconSprites = ProgressIcon.createSprites(iconName);
        this.progressIconX = progressIconX;
        this.progressIconY = progressIconY;
        this.progressArrowType = progressArrowType;
        this.progressArrows = progressArrows;
        this.progressArrowX = progressArrowX;
        this.progressArrowY = progressArrowY;
        this.progressArrowSpacing = progressArrowSpacing;
    }

    @Override
    protected void addMachinePanels() {
        addMachinePanel(new InfoPanel(menu.getBlockEntity()), PanelSide.LEFT);
        addMachinePanel(new UpgradePanel(menu), PanelSide.RIGHT);
        addMachinePanel(new SettingsPanel(menu), PanelSide.RIGHT);
    }

    protected final void addProgressSprite(ProgressSprite progressSprite) {
        progressSprites.add(progressSprite);
    }

    @Override
    protected void init() {
        super.init();
        progressSprites.clear();
        addProgressSprite(new ProgressIcon(progressIconSprites, menu, this.leftPos + progressIconX,
                this.topPos + progressIconY));
        for (int i = 0; i < progressArrows; i++) {
            addProgressSprite(ProgressArrowFactory.create(progressArrowType, menu,
                    this.leftPos + progressArrowX,
                    this.topPos + progressArrowY + i * progressArrowSpacing));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        for (ProgressSprite progressSprite : progressSprites) {
            progressSprite.render(guiGraphics);
        }
    }
}
