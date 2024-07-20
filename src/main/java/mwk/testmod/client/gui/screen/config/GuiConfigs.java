package mwk.testmod.client.gui.screen.config;

import mwk.testmod.TestMod;
import mwk.testmod.client.gui.widgets.progress.ProgressArrowFactory;
import net.minecraft.resources.ResourceLocation;

public class GuiConfigs {

	// Default values
	private static final int IMAGE_WIDTH = 176;
	private static final int IMAGE_HEIGHT = 193;
	private static final int ENERGY_BAR_X = 7;
	private static final int ENERGY_BAR_Y = 27;
	private static final int PROGRESS_ICON_X = 81;
	private static final int PROGRESS_ICON_Y = 46;
	// Parallel 3x3
	private static final int IMAGE_WIDTH_3X3 = 188;
	private static final int PROGRESS_ICON_X_3X3 = 95;
	private static final int PROGRESS_ARROWS_3X3 = 2;
	private static final int PROGRESS_ARROW_X_3X3 = 93;
	private static final int PROGRESS_ARROW_Y_3X3 = 27;
	private static final int PROGRESS_ARROW_SPACING_3X3 = 40;

	public static final GuiConfig INDUCTION_FURNACE = new GuiConfig(
			new ResourceLocation(TestMod.MODID, "textures/gui/container/3x3_parallel.png"),
			ENERGY_BAR_X, ENERGY_BAR_Y, IMAGE_WIDTH_3X3, IMAGE_HEIGHT, "smelting",
			PROGRESS_ICON_X_3X3, PROGRESS_ICON_Y, ProgressArrowFactory.Type.SINGLE,
			PROGRESS_ARROWS_3X3, PROGRESS_ARROW_X_3X3, PROGRESS_ARROW_Y_3X3,
			PROGRESS_ARROW_SPACING_3X3);

	public static final GuiConfig CRUSHER = new GuiConfig(
			new ResourceLocation(TestMod.MODID, "textures/gui/container/3x3_parallel.png"),
			ENERGY_BAR_X, ENERGY_BAR_Y, IMAGE_WIDTH_3X3, IMAGE_HEIGHT, "crushing",
			PROGRESS_ICON_X_3X3, PROGRESS_ICON_Y, ProgressArrowFactory.Type.SINGLE,
			PROGRESS_ARROWS_3X3, PROGRESS_ARROW_X_3X3, PROGRESS_ARROW_Y_3X3,
			PROGRESS_ARROW_SPACING_3X3);

	public static final GuiConfig SEPARATOR = new GuiConfig(
			new ResourceLocation(TestMod.MODID, "textures/gui/container/separator.png"),
			ENERGY_BAR_X, ENERGY_BAR_Y, IMAGE_WIDTH, IMAGE_HEIGHT, "separation", PROGRESS_ICON_X,
			PROGRESS_ICON_Y, ProgressArrowFactory.Type.ONE_TO_THREE, 1, 70, 29, 0);

	public static final GuiConfig REDSTONE_GENERATOR = new GuiConfig(
			new ResourceLocation(TestMod.MODID, "textures/gui/container/redstone_generator.png"),
			ENERGY_BAR_X, ENERGY_BAR_Y, IMAGE_WIDTH, IMAGE_HEIGHT, "energy", 45, 63,
			ProgressArrowFactory.Type.SINGLE, 0, 0, 0, 0);

}
