package mwk.testmod.client.gui.screen.base;

import mwk.testmod.client.gui.widgets.ProgressArrowSingle;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ParallelCrafterMachineScreen<T extends CrafterMachineMenu>
        extends CrafterMachineScreen<T> {

    private final ProgressArrowSingle[] progressArrows;
    private final int progressArrowX;
    private final int progressArrowY;
    private final int progressArrowSpacing;

    public ParallelCrafterMachineScreen(T menu, Inventory playerInventory, Component title,
            ResourceLocation texture, int energyBarX, int energyBarY, int imageWidth,
            int imageHeight, String iconName, int progressIconX, int progressIconY,
            int progressArrows, int progressArrowX, int progressArrowY, int progressArrowSpacing) {
        super(menu, playerInventory, title, texture, energyBarX, energyBarY, imageWidth,
                imageHeight, iconName, progressIconX, progressIconY);
        this.progressArrows = new ProgressArrowSingle[progressArrows];
        this.progressArrowX = progressArrowX;
        this.progressArrowY = progressArrowY;
        this.progressArrowSpacing = progressArrowSpacing;
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < progressArrows.length; i++) {
            progressArrows[i] = new ProgressArrowSingle(menu, this.leftPos + progressArrowX,
                    this.topPos + progressArrowY + i * progressArrowSpacing);
        }
    }

    @Override
    protected void renderProgress(GuiGraphics guiGraphics) {
        for (ProgressArrowSingle progressArrow : progressArrows) {
            progressArrow.render(guiGraphics);
        }
    }
}
