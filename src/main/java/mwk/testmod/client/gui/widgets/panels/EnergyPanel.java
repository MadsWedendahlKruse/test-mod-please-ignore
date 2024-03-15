package mwk.testmod.client.gui.widgets.panels;

import java.util.Locale;
import com.ibm.icu.text.NumberFormat;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.base.CrafterMachineMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnergyPanel extends MachinePanel {

	public static final ResourceLocation ICON =
			new ResourceLocation(TestMod.MODID, "widget/icon_energy");
	public static final float[] COLOR = new float[] {1, 0, 0.5F, 1};

	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

	private final CrafterMachineMenu menu;

	public EnergyPanel(CrafterMachineMenu menu) {
		super(70, DEFAULT_ICON_HEIGHT + 3 * DEFAULT_ICON_PADDING + 2 * LINE_HEIGHT,
				Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY), COLOR,
				ICON, DEFAULT_ICON_PADDING, DEFAULT_ICON_PADDING);
		this.menu = menu;
	}

	@Override
	public void renderOpen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int x = getOpenLeft();
		int y = getOpenTop();
		guiGraphics.drawString(font,
				Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_USE_TITLE),
				x, y, 0x404040, false);
		guiGraphics.drawString(font,
				Component.translatable(TestModLanguageProvider.KEY_WIDGET_PANEL_ENERGY_USE_TEXT,
						NUMBER_FORMAT.format(menu.getEnergyPerTick())),
				x, y + LINE_HEIGHT, 0xffffff, false);
	}

}
