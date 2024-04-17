package mwk.testmod.client.gui.widgets.panels;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.base.BaseMachineMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class UpgradePanel extends MachinePanel {

    public static final ResourceLocation ICON =
            new ResourceLocation(TestMod.MODID, "widget/icon_upgrade");
    public static final ResourceLocation ITEM_SLOT =
            new ResourceLocation(TestMod.MODID, "widget/item_slot");
    public static final float[] COLOR = new float[] {0.5F, 0.75F, 0.5F, 1};

    public static final int ITEM_SLOT_SIZE = 18;
    public static final int PADDING = 5;

    private final BaseMachineMenu menu;
    private final int upgradeRows;
    private final int upgradeColumns;

    public UpgradePanel(BaseMachineMenu menu, int upgradeRows, int upgradeColumns) {
        super(upgradeColumns * ITEM_SLOT_SIZE + 2 * PADDING,
                upgradeRows * ITEM_SLOT_SIZE + 2 * PADDING,
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_UPGRADE), COLOR,
                ICON);
        this.menu = menu;
        this.upgradeRows = upgradeRows;
        this.upgradeColumns = upgradeColumns;
    }

    @Override
    protected void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // -1 to account for the border of the item slot sprite
        int startX = getOpenLeft() + PADDING - 1;
        int startY = getOpenTop() + PADDING - 1;
        for (int i = 0; i < menu.upgradeSlots; i++) {
            int x = startX + (i % upgradeColumns) * ITEM_SLOT_SIZE;
            int y = startY + (i / upgradeColumns) * ITEM_SLOT_SIZE;
            guiGraphics.blitSprite(ITEM_SLOT, x, y, ITEM_SLOT_SIZE, ITEM_SLOT_SIZE);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        // TODO: Not super efficient to check this every frame
        // SLots in the GUI use a different coordinate system than the screen
        menu.setUpgradesVisible(isOpenFully(), getOpenLeft() - screenX + PADDING,
                getOpenTop() - screenY + PADDING);
    }

}
