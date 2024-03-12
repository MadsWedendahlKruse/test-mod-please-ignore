package mwk.testmod.client.gui.screen.base;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.ProgresArrow;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ParallelCrafterMachineScreen<T extends CrafterMachineMenu>
        extends CrafterMachineScreen<T> {

    private final ProgresArrow[] progressArrows;
    private final int progressArrowX;
    private final int progressArrowY;
    private final int progressArrowSpacing;

    public ParallelCrafterMachineScreen(T menu, Inventory playerInventory, Component title,
            String iconName, ParallelCraftingMachineScreenPreset preset) {
        this(menu, playerInventory, title, preset.texture, preset.energyBarX, preset.energyBarY,
                preset.imageWidth, preset.imageHeight, iconName, preset.progressIconX,
                preset.progressIconY, preset.progressArrows, preset.progressArrowX,
                preset.progressArrowY, preset.progressArrowSpacing);
    }

    public ParallelCrafterMachineScreen(T menu, Inventory playerInventory, Component title,
            ResourceLocation texture, int energyBarX, int energyBarY, int imageWidth,
            int imageHeight, String iconName, int progressIconX, int progressIconY,
            int progressArrows, int progressArrowX, int progressArrowY, int progressArrowSpacing) {
        super(menu, playerInventory, title, texture, energyBarX, energyBarY, imageWidth,
                imageHeight, iconName, progressIconX, progressIconY);
        this.progressArrows = new ProgresArrow[progressArrows];
        this.progressArrowX = progressArrowX;
        this.progressArrowY = progressArrowY;
        this.progressArrowSpacing = progressArrowSpacing;
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < progressArrows.length; i++) {
            progressArrows[i] = new ProgresArrow(menu, this.leftPos + progressArrowX,
                    this.topPos + progressArrowY + i * progressArrowSpacing);
        }
    }

    @Override
    protected void renderProgress(GuiGraphics guiGraphics) {
        for (ProgresArrow progressArrow : progressArrows) {
            progressArrow.render(guiGraphics);
        }
    }

    public static class ParallelCraftingMachineScreenPreset extends CrafterMachineScreenPreset {

        public final int progressArrows;
        public final int progressArrowX;
        public final int progressArrowY;
        public final int progressArrowSpacing;

        public ParallelCraftingMachineScreenPreset(int energyBarX, int energyBarY,
                ResourceLocation texture, int imageWidth, int imageHeight, String iconName,
                int progressIconX, int progressIconY, int progressArrows, int progressArrowX,
                int progressArrowY, int progressArrowSpacing) {
            super(energyBarX, energyBarY, texture, imageWidth, imageHeight, iconName, progressIconX,
                    progressIconY);
            this.progressArrows = progressArrows;
            this.progressArrowX = progressArrowX;
            this.progressArrowY = progressArrowY;
            this.progressArrowSpacing = progressArrowSpacing;
        }
    }

    public static final ParallelCraftingMachineScreenPreset PRESET_3X3_PARALLEL =
            new ParallelCraftingMachineScreenPreset(7, 27,
                    new ResourceLocation(TestMod.MODID, "textures/gui/container/3x3_parallel.png"),
                    188, 193, "fire", 95, 46, 2, 93, 27, 40);
}
