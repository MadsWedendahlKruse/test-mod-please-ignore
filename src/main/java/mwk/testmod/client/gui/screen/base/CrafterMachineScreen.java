package mwk.testmod.client.gui.screen.base;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.ProgressIcon;
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
            CrafterMachineScreenPreset preset) {
        this(menu, playerInventory, title, preset.texture, preset.energyBarX, preset.energyBarY,
                preset.imageWidth, preset.imageHeight, preset.iconName, preset.progressIconX,
                preset.progressIconY);
    }

    public CrafterMachineScreen(T menu, Inventory playerInventory, Component title,
            ResourceLocation texture, int energyBarX, int energyBarY, int imageWidth,
            int imageHeight, String iconName, int progressIconX, int progressIconY) {
        super(menu, playerInventory, title, texture, energyBarX, energyBarY, imageWidth,
                imageHeight);
        this.progressIconSprites = new WidgetSprites(
                new ResourceLocation(TestMod.MODID, "container/progress/" + iconName),
                new ResourceLocation(TestMod.MODID,
                        "container/progress/" + iconName + "_background"));
        this.progressIconX = progressIconX;
        this.progressIconY = progressIconY;
    }

    protected abstract void renderProgress(GuiGraphics guiGraphics);

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

    public static class CrafterMachineScreenPreset extends BaseMachineScreenPreset {

        public final String iconName;
        public final int progressIconX;
        public final int progressIconY;

        public CrafterMachineScreenPreset(int energyBarX, int energyBarY, ResourceLocation texture,
                int imageWidth, int imageHeight, String iconName, int progressIconX,
                int progressIconY) {
            super(energyBarX, energyBarY, texture, imageWidth, imageHeight);
            this.iconName = iconName;
            this.progressIconX = progressIconX;
            this.progressIconY = progressIconY;
        }
    }
}
