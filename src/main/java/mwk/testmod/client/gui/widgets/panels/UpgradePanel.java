package mwk.testmod.client.gui.widgets.panels;

import mwk.testmod.TestMod;
import mwk.testmod.client.utils.GuiUtils;
import mwk.testmod.client.gui.widgets.panels.base.MachinePanel;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import mwk.testmod.client.utils.ItemSlotGridHelper;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class UpgradePanel extends MachinePanel {

    public static final ResourceLocation ICON =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/icon_upgrade");
    public static final float[] COLOR = new float[]{0.5F, 0.75F, 0.5F, 1};
    public static final int PADDING = 5;

    private static final ItemSlotGridHelper GRID_HELPER = ItemSlotGridHelper.ROWS_2;

    private final MachineMenu menu;

    public UpgradePanel(MachineMenu menu) {
        super(GRID_HELPER.getWidth(menu.upgradeSlots) + 2 * PADDING,
                GRID_HELPER.getHeight(menu.upgradeSlots) + 2 * PADDING,
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_UPGRADE), COLOR,
                ICON);
        this.menu = menu;
    }

    @Override
    protected void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int startX = getOpenLeft() + PADDING;
        int startY = getOpenTop() + PADDING;
        for (int i = 0; i < menu.upgradeSlots; i++) {
            ItemSlotGridHelper.SlotPosition slotPosition =
                    GRID_HELPER.getSlotPosition(startX, startY, i);
            GuiUtils.renderItemSlot(guiGraphics, slotPosition.x(), slotPosition.y());
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
