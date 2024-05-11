package mwk.testmod.client.gui.widgets.panels;

import java.util.ArrayList;
import java.util.List;
import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.OnOffButton;
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
        autoInsertButton = new OnOffButton(BUTTON_SIZE, (button) -> {
            menu.setAutoInsert(!menu.isAutoInsert());
        }, "auto_insert", "auto_insert");
        autoEjecButton = new OnOffButton(BUTTON_SIZE, (button) -> {
            menu.setAutoEject(!menu.isAutoEject());
        }, "auto_eject", "auto_eject");
        buttons.add(autoInsertButton);
        buttons.add(autoEjecButton);
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
