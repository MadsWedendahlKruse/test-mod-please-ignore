package mwk.testmod.client.gui.widgets.panels;

import java.util.ArrayList;
import java.util.List;
import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.buttons.OnOffButton;
import mwk.testmod.client.gui.widgets.panels.base.MachinePanel;
import mwk.testmod.client.gui.widgets.panels.base.PanelSide;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SettingsPanel extends MachinePanel {

    public static final ResourceLocation ICON =
            new ResourceLocation(TestMod.MODID, "widget/icon_settings");
    public static final float[] COLOR = new float[] {1.0F, 0.85F, 0, 1};

    public static final int BUTTON_SIZE = 20;
    public static final int BUTTON_PADDING = 5;

    private final MachineMenu menu;

    private List<AbstractButton> buttons = new ArrayList<>();
    private OnOffButton autoEjectButton;
    private OnOffButton autoInsertButton;

    public SettingsPanel(MachineMenu menu) {
        super(0, 2 * LINE_HEIGHT,
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_SETTINGS), COLOR,
                ICON);
        this.menu = menu;
        autoEjectButton = new OnOffButton(BUTTON_SIZE, (button) -> {
            menu.setAutoEject(!menu.isAutoEject());
        }, "auto_eject_on", "auto_eject_off",
                TestModLanguageProvider.KEY_WIDGET_AUTO_EJECT_TOOLTIP);
        autoInsertButton = new OnOffButton(BUTTON_SIZE, (button) -> {
            menu.setAutoInsert(!menu.isAutoInsert());
        }, "auto_insert_on", "auto_insert_off",
                TestModLanguageProvider.KEY_WIDGET_AUTO_INSERT_TOOLTIP);
        if (menu.getBlockEntity().getOutputSlots() > 0) {
            buttons.add(autoEjectButton);
        }
        buttons.add(autoInsertButton);
    }

    @Override
    public void setPosition(int x, int y, PanelSide side) {
        super.setPosition(x, y, side);
        int buttonX = getOpenLeft();
        int buttonY = getOpenTop();
        for (int i = 0; i < buttons.size(); i++) {
            AbstractButton button = buttons.get(i);
            button.setPosition(buttonX + i * (BUTTON_SIZE + BUTTON_PADDING), buttonY);
        }
    }

    @Override
    protected void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (AbstractButton button : buttons) {
            button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        // There's something going on with the synching. I think the auto eject/insert values are
        // being sent after the constructor is called, so we somehow need to set them again at
        // another point in time. This is a temporary fix.
        if (menu.isAutoEject() != autoEjectButton.isOn()) {
            autoEjectButton.setOn(menu.isAutoEject());
        }
        if (menu.isAutoInsert() != autoInsertButton.isOn()) {
            autoInsertButton.setOn(menu.isAutoInsert());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AbstractButton buttonWidget : buttons) {
            if (buttonWidget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

}
