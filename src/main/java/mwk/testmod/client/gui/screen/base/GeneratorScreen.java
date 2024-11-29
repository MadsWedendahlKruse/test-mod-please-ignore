package mwk.testmod.client.gui.screen.base;

import java.util.Locale;
import mwk.testmod.client.gui.screen.config.GuiConfig;
import mwk.testmod.client.utils.ColorUtils;
import mwk.testmod.client.utils.GuiUtils;
import mwk.testmod.client.utils.GuiUtils.GuiTextElement;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.inventory.base.ProcessingMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GeneratorScreen<T extends ProcessingMenu> extends ProcessingScreen<T> {

    public static final int TEXT_LEFT = 94;
    public static final int TEXT_TOP = 25;

    private final String generatorTitle;
    private final String generatorText;
    private final String generatingText;

    public GeneratorScreen(T menu, Inventory playerInventory, Component title, GuiConfig config) {
        super(menu, playerInventory, title, config);
        // Change the text based on what resource is being generated
        MachineBlockEntity generator = menu.getBlockEntity();
        String generatorTitle = "";
        String generatorText = "";
        String generatingText = "";
        if (generator.energy().isPresent()) {
            generatorTitle = TestModLanguageProvider.KEY_WIDGET_GENERATOR_TITLE_ENERGY;
            generatorText = TestModLanguageProvider.KEY_WIDGET_GENERATOR_TEXT_ENERGY;
            generatingText = TestModLanguageProvider.KEY_WIDGET_GENERATOR_GENERATING_TEXT_ENERGY;
        }
        if (generator.temporalFlux().isPresent()) {
            generatorTitle = TestModLanguageProvider.KEY_WIDGET_GENERATOR_TITLE_TEMPORAL_FLUX;
            generatorText = TestModLanguageProvider.KEY_WIDGET_GENERATOR_TEXT_TEMPORAL_FLUX;
            generatingText = TestModLanguageProvider.KEY_WIDGET_GENERATOR_GENERATING_TEXT_TEMPORAL_FLUX;
        }
        this.generatorTitle = generatorTitle;
        this.generatorText = generatorText;
        this.generatingText = generatingText;
    }

    private static String ticksToClock(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
    }

    protected GuiTextElement[] getTextElements(ProcessingMenu menu) {
        int energyPerTick = menu.getEnergyPerTick();
        int remainingTicks = menu.getMaxProgress() - menu.getProgress();
        String ticksString = ticksToClock(remainingTicks);
        if (remainingTicks == Integer.MAX_VALUE) {
            energyPerTick = 0;
            ticksString = "--:--";
        }
        int totalEnergy = energyPerTick * menu.getMaxProgress();

        GuiTextElement[] elements = new GuiTextElement[3];
        elements[0] = new GuiTextElement(generatorTitle, generatorText,
                GuiUtils.NUMBER_FORMAT.format(totalEnergy));
        elements[1] =
                new GuiTextElement(TestModLanguageProvider.KEY_WIDGET_GENERATOR_GENERATING_TITLE,
                        generatingText, GuiUtils.NUMBER_FORMAT.format(energyPerTick));
        elements[2] =
                new GuiTextElement(TestModLanguageProvider.KEY_WIDGET_GENERATOR_DURATION_TITLE,
                        TestModLanguageProvider.KEY_WIDGET_GENERATOR_DURATION_TEXT, ticksString);
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
