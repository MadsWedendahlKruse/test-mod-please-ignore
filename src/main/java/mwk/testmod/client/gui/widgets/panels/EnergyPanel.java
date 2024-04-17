package mwk.testmod.client.gui.widgets.panels;

import java.util.Locale;
import com.ibm.icu.text.NumberFormat;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import mwk.testmod.common.util.ColorUtils;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnergyPanel extends MachinePanel {

	public static final ResourceLocation ICON =
			new ResourceLocation(TestMod.MODID, "widget/icon_energy");
	public static final float[] COLOR = new float[] {1, 0, 0, 1};

	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
	private static final int SPACE_WIDTH = 4;
	private static final int TITLE_COLOR = ColorUtils.TEXT_YELLOW;
	private static final int TEXT_COLOR = ColorUtils.TEXT_WHITE;

	private final CrafterMachineMenu menu;

	private static class PanelTextElement {
		public final Component title;
		public final Component text;

		public PanelTextElement(String titleKey, String textKey, Object... args) {
			this.title = Component.translatable(titleKey);
			this.text = Component.translatable(textKey, args);
		}
	}

	public EnergyPanel(CrafterMachineMenu menu) {
		super(getMaxLineWidth(menu) + 2, getTextElements(menu).length * 2 * LINE_HEIGHT,
				Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY), COLOR,
				ICON);
		this.menu = menu;
	}

	private static PanelTextElement[] getTextElements(CrafterMachineMenu menu) {
		int energyModifier = 100 * menu.getEnergyPerTick() / menu.energyPerTickBase;
		String craftingSpeedSeconds =
				String.format(Locale.US, "%.2f", (float) menu.getMaxProgress() / 20);

		return new PanelTextElement[] {
				new PanelTextElement(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_CAPACITY_TITLE,
						TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_CAPACITY_TEXT,
						NUMBER_FORMAT.format(menu.getMaxEnergy())),
				new PanelTextElement(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_USAGE_TITLE,
						TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_USAGE_TEXT,
						NUMBER_FORMAT.format(menu.getEnergyPerTick()), energyModifier),
				new PanelTextElement(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_CRAFTING_TITLE,
						TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_CRAFTING_TEXT,
						NUMBER_FORMAT.format(menu.getMaxProgress()), craftingSpeedSeconds)};
	}

	private static int getMaxLineWidth(CrafterMachineMenu menu) {
		int max = 0;
		for (PanelTextElement element : getTextElements(menu)) {
			int elementMax =
					Math.max(font.width(element.title), font.width(element.text) + SPACE_WIDTH);
			max = Math.max(max, elementMax);
		}
		return max;
	}

	@Override
	public void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int x = getOpenLeft();
		int y = getOpenTop();
		final PanelTextElement[] elements = getTextElements(menu);
		for (int i = 0; i < elements.length; i++) {
			PanelTextElement element = elements[i];
			guiGraphics.drawString(font, element.title, x, y + (2 * i) * LINE_HEIGHT, TITLE_COLOR,
					true);
			guiGraphics.drawString(font, element.text, x + SPACE_WIDTH,
					y + (2 * i + 1) * LINE_HEIGHT, TEXT_COLOR, false);
		}
	}
}
