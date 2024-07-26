package mwk.testmod.client.gui.screen.base;

import java.util.Locale;
import mwk.testmod.client.gui.GuiUtils;
import mwk.testmod.client.gui.GuiUtils.GuiTextElement;
import mwk.testmod.client.gui.screen.config.GuiConfig;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import mwk.testmod.common.util.ColorUtils;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GeneratorScreen<T extends ProcessingMenu> extends ProcessingScreen<T> {

    public static final int TEXT_LEFT = 94;
    public static final int TEXT_TOP = 25;

    public GeneratorScreen(T menu, Inventory playerInventory, Component title, GuiConfig config) {
        super(menu, playerInventory, title, config);
    }

    private static String ticksToClock(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
    }

    protected static GuiTextElement[] getTextElements(ProcessingMenu menu) {
        int energyPerTick = menu.getEnergyPerTick();
        int remainingTicks = menu.getMaxProgress() - menu.getProgress();
        // TOOO: Super dirty hacks
        int energyGeneratedPerTick = energyPerTick / 2;
        String ticksString = ticksToClock(remainingTicks);
        if (remainingTicks == Integer.MAX_VALUE) {
            energyGeneratedPerTick = 0;
            ticksString = "--:--";
        }

        GuiTextElement[] elements = new GuiTextElement[3];
        elements[0] =
                new GuiTextElement(TestModLanguageProvider.KEY_WIDGET_GENERATOR_GENERATING_TITLE,
                        TestModLanguageProvider.KEY_WIDGET_GENERATOR_GENERATING_TEXT,
                        GuiUtils.NUMBER_FORMAT.format(energyGeneratedPerTick));
        elements[1] =
                new GuiTextElement(TestModLanguageProvider.KEY_WIDGET_GENERATOR_DURATION_TITLE,
                        TestModLanguageProvider.KEY_WIDGET_GENERATOR_DURATION_TEXT, ticksString);
        elements[2] = new GuiTextElement(TestModLanguageProvider.KEY_WIDGET_GENERATOR_OUTPUT_TITLE,
                TestModLanguageProvider.KEY_WIDGET_GENERATOR_OUTPUT_TEXT,
                GuiUtils.NUMBER_FORMAT.format(energyPerTick));
        return elements;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        GuiUtils.renderTextElements(guiGraphics, font, getTextElements(menu),
                this.leftPos + TEXT_LEFT, this.topPos + TEXT_TOP, ColorUtils.TEXT_YELLOW,
                ColorUtils.TEXT_WHITE, font.lineHeight + 1);
    }

}
