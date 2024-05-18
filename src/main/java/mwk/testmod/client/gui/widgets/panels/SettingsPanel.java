package mwk.testmod.client.gui.widgets.panels;

import java.util.ArrayList;
import java.util.List;
import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.buttons.OnOffButton;
import mwk.testmod.common.block.inventory.base.BaseMachineMenu;
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

    private final BaseMachineMenu menu;

    private List<AbstractButton> buttons = new ArrayList<>();
    private OnOffButton autoEjecButton;
    private OnOffButton autoInsertButton;

    public SettingsPanel(BaseMachineMenu menu) {
        super(0, 2 * LINE_HEIGHT,
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_SETTINGS), COLOR,
                ICON);
        this.menu = menu;
        autoEjecButton = new OnOffButton(BUTTON_SIZE, (button) -> {
            menu.setAutoEject(!menu.isAutoEject());
        }, "auto_eject_on", "auto_eject_off",
                TestModLanguageProvider.KEY_WIDGET_AUTO_EJECT_TOOLTIP);
        autoInsertButton = new OnOffButton(BUTTON_SIZE, (button) -> {
            menu.setAutoInsert(!menu.isAutoInsert());
        }, "auto_insert_on", "auto_insert_off",
                TestModLanguageProvider.KEY_WIDGET_AUTO_INSERT_TOOLTIP);
        buttons.add(autoEjecButton);
        buttons.add(autoInsertButton);
    }

    @Override
    public void setPosition(int x, int y, boolean left) {
        super.setPosition(x, y, left);
        int buttonX = getOpenLeft() + 5;
        int buttonY = getOpenTop();
        autoEjecButton.setPosition(buttonX, buttonY);
        autoInsertButton.setPosition(buttonX + BUTTON_SIZE + 5, buttonY);
    }

    @Override
    protected void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        autoEjecButton.render(guiGraphics, mouseX, mouseY, partialTick);
        autoInsertButton.render(guiGraphics, mouseX, mouseY, partialTick);
        // There's something going on with the synching. I think the auto eject/insert values are
        // being sent after the constructor is called, so we somehow need to set them again at
        // another point in time. This is a temporary fix.
        if (menu.isAutoEject() != autoEjecButton.isOn()) {
            autoEjecButton.setOn(menu.isAutoEject());
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
