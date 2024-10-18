package mwk.testmod.client.gui.widgets.panels;

import java.util.Locale;
import mwk.testmod.TestMod;
import mwk.testmod.client.utils.GuiUtils;
import mwk.testmod.client.utils.GuiUtils.GuiTextElement;
import mwk.testmod.client.gui.widgets.panels.base.MachinePanel;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import mwk.testmod.client.utils.ColorUtils;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnergyPanel extends MachinePanel {

    public static final ResourceLocation ICON =
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "widget/icon_energy");
    public static final float[] COLOR = new float[]{0.8F, 0, 0, 1};

    private static final int SPACE_WIDTH = 4;
    private static final int TITLE_COLOR = ColorUtils.TEXT_YELLOW;
    private static final int TEXT_COLOR = ColorUtils.TEXT_WHITE;

    private final ProcessingMenu menu;

    public EnergyPanel(ProcessingMenu menu) {
        super(getMaxLineWidth(menu) + 2, getTextElements(menu).length * 2 * LINE_HEIGHT,
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY), COLOR,
                ICON);
        this.menu = menu;
    }

    private static GuiTextElement[] getTextElements(ProcessingMenu menu) {
        int energyModifier = 100 * menu.getEnergyPerTick() / menu.energyPerTickBase;
        String craftingSpeedSeconds =
                String.format(Locale.US, "%.2f", (float) menu.getMaxProgress() / 20);

        GuiTextElement[] elements = new GuiTextElement[3];
        elements[0] =
                new GuiTextElement(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_CAPACITY_TITLE,
                        TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_CAPACITY_TEXT,
                        GuiUtils.NUMBER_FORMAT.format(menu.getMaxEnergy()));
        elements[1] =
                new GuiTextElement(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_USAGE_TITLE,
                        TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_USAGE_TEXT,
                        GuiUtils.NUMBER_FORMAT.format(menu.getEnergyPerTick()), energyModifier);
        elements[2] =
                new GuiTextElement(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_CRAFTING_TITLE,
                        TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_CRAFTING_TEXT,
                        GuiUtils.NUMBER_FORMAT.format(menu.getMaxProgress()), craftingSpeedSeconds);
        return elements;
    }

    private static int getMaxLineWidth(ProcessingMenu menu) {
        int max = 0;
        for (GuiTextElement element : getTextElements(menu)) {
            int elementMax =
                    Math.max(font.width(element.title), font.width(element.text) + SPACE_WIDTH);
            max = Math.max(max, elementMax);
        }
        return max;
    }

    @Override
    public void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // int x = getOpenLeft();
        // int y = getOpenTop();
        // final GuiTextElement[] elements = getTextElements(menu);
        // for (int i = 0; i < elements.length; i++) {
        // GuiTextElement element = elements[i];
        // guiGraphics.drawString(font, element.title, x, y + (2 * i) * LINE_HEIGHT, TITLE_COLOR,
        // true);
        // guiGraphics.drawString(font, element.text, x + SPACE_WIDTH,
        // y + (2 * i + 1) * LINE_HEIGHT, TEXT_COLOR, false);
        // }
        GuiUtils.renderTextElements(guiGraphics, font, getTextElements(menu), getOpenLeft(),
                getOpenTop(), TITLE_COLOR, TEXT_COLOR, LINE_HEIGHT);
    }
}
