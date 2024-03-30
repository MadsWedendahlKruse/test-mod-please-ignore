package mwk.testmod.datagen;

import mwk.testmod.TestMod;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class TestModLanguageProvider extends LanguageProvider {

	// Info
	public static final String KEY_INFO_CONTROLLER_BLUEPRINT_HELP =
			"info.testmod.controller.blueprint.help";
	public static final String KEY_INFO_CONTROLLER_BLUEPRINT_COMPLETE =
			"info.testmod.controller.blueprint.complete";
	public static final String KEY_INFO_CONTROLLER_BLUEPRINT_INSUFFICIENTS =
			"info.testmod.controller.blueprint.insufficient_blocks";
	public static final String KEY_INFO_CONTROLLER_BLUEPRINTED =
			"info.testmod.controller.blueprint.blocked";
	public static final String KEY_INFO_CONTROLLER_MISSINGS =
			"info.testmod.controller.blueprint.hologram.missing_blocks";
	public static final String KEY_INFO_CONTROLLER_INCORRECTS =
			"info.testmod.controller.blueprint.hologram.incorrect_blocks";
	public static final String KEY_INFO_CONTROLLER_HOLOGRAM_COMPLETE =
			"info.testmod.controller.blueprint.hologram.complete";
	public static final String KEY_INFO_CONTROLLER_HOLOGRAM_RIGHT_CLICK_ME =
			"info.testmod.controller.blueprint.hologram.right_click_me";
	public static final String KEY_INFO_HOLOGRAM_PROJECTOR_HELP =
			"info.testmod.hologram_projector.blueprint.help";
	public static final String KEY_INFO_HOLOGRAM_PROJECTOR_LOCKED =
			"info.testmod.hologram_projector.blueprint.locked";
	public static final String KEY_INFO_HOLOGRAM_PROJECTOR_UNLOCKED =
			"info.testmod.hologram_projector.blueprint.unlocked";
	// Widgets
	public static final String KEY_WIDGET_HOLOGRAM_PROJECTOR_NONE =
			"widget.testmod.hologram_projector.none";
	public static final String KEY_WIDGET_HOLOGRAM_PROJECTOR_BLUEPRINTS =
			"widget.testmod.hologram_projector.blueprints";
	public static final String KEY_WIDGET_HOLOGRAM_PROJECTOR_BUTTONS =
			"widget.testmod.hologram_projector.buttons";
	public static final String KEY_WIDGET_ENERGY_BAR = "widget.testmod.energy_bar";
	public static final String KEY_WIDGET_ENERGY_BAR_HOVER = "widget.testmod.energy_bar.hover";
	public static final String KEY_WIDGET_PANEL_ENERGY = "widget.testmod.panel.energy";
	public static final String KEY_WIDGET_PANEL_ENERGY_USE_TITLE =
			"widget.testmod.panel.energy_use_title";
	public static final String KEY_WIDGET_PANEL_ENERGY_USE_TEXT =
			"widget.testmod.panel.energy_use_text";
	// Subtitles
	public static final String KEY_SUBTITLE_MULTIBLOCK_FORM =
			"sounds.testmod.block.multiblock_form";
	public static final String KEY_SUBTITLE_MULTIBLOCK_CRUSHER = "sounds.testmod.block.crusher";

	public TestModLanguageProvider(PackOutput output, String locale) {
		super(output, TestMod.MODID, locale);
	}

	@Override
	protected void addTranslations() {
		// Blocks
		add(TestModBlocks.MACHINE_FRAME_BASIC.get(), "Basic Machine Frame");
		add(TestModBlocks.MACHINE_FRAME_REINFORCED.get(), "Reinforced Machine Frame");
		add(TestModBlocks.MACHINE_FRAME_ADVANCED.get(), "Advanced Machine Frame");
		add(TestModBlocks.SUPER_ASSEMBLER.get(), "Super Assembler");
		add(TestModBlocks.INDUCTION_FURNACE.get(), "Induction Furnace");
		add(TestModBlocks.CRUSHER.get(), "Crusher");
		add(TestModBlocks.SEPARATOR.get(), "Separator");
		// Items
		add(TestModItems.WRENCH_ITEM.get(), "Wrench");
		add(TestModItems.HOLOGRAM_PROJECTOR_ITEM.get(), "Hologram Projector");
		// Info
		add(KEY_INFO_CONTROLLER_BLUEPRINT_HELP, "Right-click with a wrench to see the blueprint");
		add(KEY_INFO_CONTROLLER_BLUEPRINT_COMPLETE,
				"The blueprint is complete, form the multiblock with a wrench");
		add(KEY_INFO_CONTROLLER_BLUEPRINT_INSUFFICIENTS,
				"You are missing the required blocks to build the blueprint");
		add(KEY_INFO_CONTROLLER_BLUEPRINTED,
				"An incorrect block is in the way of the blueprint [x=%s, y=%s, z=%s]");
		add(KEY_INFO_CONTROLLER_MISSINGS, "Missing blocks:");
		add(KEY_INFO_CONTROLLER_INCORRECTS, "Incorrect blocks:");
		add(KEY_INFO_CONTROLLER_HOLOGRAM_COMPLETE, "Blueprint complete!");
		add(KEY_INFO_CONTROLLER_HOLOGRAM_RIGHT_CLICK_ME, "Right-click me with a wrench");
		add(KEY_INFO_HOLOGRAM_PROJECTOR_HELP, "Shift + right-click to choose a blueprint");
		add(KEY_INFO_HOLOGRAM_PROJECTOR_LOCKED, "Blueprint position locked");
		add(KEY_INFO_HOLOGRAM_PROJECTOR_UNLOCKED, "Blueprint position unlocked");
		// Widgets
		add(KEY_WIDGET_HOLOGRAM_PROJECTOR_NONE, "None");
		add(KEY_WIDGET_HOLOGRAM_PROJECTOR_BLUEPRINTS, "Blueprints");
		add(KEY_WIDGET_HOLOGRAM_PROJECTOR_BUTTONS, "Buttons");
		add(KEY_WIDGET_ENERGY_BAR, "Energy");
		add(KEY_WIDGET_ENERGY_BAR_HOVER, "%s/%s FE");
		add(KEY_WIDGET_PANEL_ENERGY, "Energy");
		add(KEY_WIDGET_PANEL_ENERGY_USE_TITLE, "Energy use");
		add(KEY_WIDGET_PANEL_ENERGY_USE_TEXT, "    %s FE/t");
		// Sounds
		add(KEY_SUBTITLE_MULTIBLOCK_FORM, "Multiblock forms");
		add(KEY_SUBTITLE_MULTIBLOCK_CRUSHER, "Crusher crushes");
	}
}
