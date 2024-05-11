package mwk.testmod.datagen;

import mwk.testmod.TestMod;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class TestModLanguageProvider extends LanguageProvider {

	// Creative tabs
	public static final String KEY_CREATIVE_TAB = "itemGroup.testmod";
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
	public static final String KEY_WIDGET_PANEL_ENERGY_USAGE_TITLE =
			"widget.testmod.panel.energy_usage_title";
	public static final String KEY_WIDGET_PANEL_ENERGY_USAGE_TEXT =
			"widget.testmod.panel.energy_usage_text";
	public static final String KEY_WIDGET_PANEL_ENERGY_CAPACITY_TITLE =
			"widget.testmod.panel.energy_capacity_title";
	public static final String KEY_WIDGET_PANEL_ENERGY_CAPACITY_TEXT =
			"widget.testmod.panel.energy_capacity_text";
	public static final String KEY_WIDGET_PANEL_ENERGY_CRAFTING_TITLE =
			"widget.testmod.panel.energy_crafting_title";
	public static final String KEY_WIDGET_PANEL_ENERGY_CRAFTING_TEXT =
			"widget.testmod.panel.energy_crafting_text";
	public static final String KEY_WIDGET_PANEL_UPGRADE = "widget.testmod.panel.upgrade";
	public static final String KEY_WIDGET_PANEL_SETTINGS = "widget.testmod.panel.settings";
	// Subtitles
	public static final String KEY_SUBTITLE_MULTIBLOCK_FORM =
			"sounds.testmod.block.multiblock_form";
	public static final String KEY_SUBTITLE_MULTIBLOCK_CRUSHER = "sounds.testmod.block.crusher";

	public TestModLanguageProvider(PackOutput output, String locale) {
		super(output, TestMod.MODID, locale);
	}

	@Override
	protected void addTranslations() {
		// Creative tabs
		add(KEY_CREATIVE_TAB, "Test Mod");
		// Blocks
		add(TestModBlocks.ILMENITE_ORE.get(), "Ilmenite Ore");
		add(TestModBlocks.DEEPSLATE_ILMENITE_ORE.get(), "Deepslate Ilmenite Ore");
		add(TestModBlocks.MACHINE_FRAME_BASIC.get(), "Basic Machine Frame");
		add(TestModBlocks.MACHINE_FRAME_REINFORCED.get(), "Reinforced Machine Frame");
		add(TestModBlocks.MACHINE_FRAME_ADVANCED.get(), "Advanced Machine Frame");
		add(TestModBlocks.MACHINE_INPUT_PORT.get(), "Machine Input Port");
		add(TestModBlocks.MACHINE_OUTPUT_PORT.get(), "Machine Output Port");
		add(TestModBlocks.MACHINE_ENERGY_PORT.get(), "Machine Energy Port");
		add(TestModBlocks.COPPER_COIL.get(), "Copper Coil");
		add(TestModBlocks.SUPER_ASSEMBLER.get(), "Super Assembler");
		add(TestModBlocks.INDUCTION_FURNACE.get(), "Induction Furnace");
		add(TestModBlocks.CRUSHER.get(), "Crusher");
		add(TestModBlocks.SEPARATOR.get(), "Separator");
		// Items
		add(TestModItems.WRENCH_ITEM.get(), "Wrench");
		add(TestModItems.HOLOGRAM_PROJECTOR_ITEM.get(), "Hologram Projector");
		add(TestModItems.RAW_ILMENITE.get(), "Raw Ilmenite");
		add(TestModItems.COAL_DUST.get(), "Coal Dust");
		add(TestModItems.IRON_DUST.get(), "Iron Dust");
		add(TestModItems.STEEL_DUST.get(), "Steel Dust");
		add(TestModItems.ILMENITE_DUST.get(), "Ilmenite Dust");
		add(TestModItems.TITANIUM_DUST.get(), "Titanium Dust");
		add(TestModItems.STEEL_INGOT.get(), "Steel Ingot");
		add(TestModItems.TITANIUM_INGOT.get(), "Titanium Ingot");
		add(TestModItems.SPEED_UPGRADE.get(), "Speed Upgrade");
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
		add(KEY_WIDGET_PANEL_ENERGY_USAGE_TITLE, "Usage");
		add(KEY_WIDGET_PANEL_ENERGY_USAGE_TEXT, "  %s FE/t (%s%%)");
		add(KEY_WIDGET_PANEL_ENERGY_CAPACITY_TITLE, "Capacity");
		add(KEY_WIDGET_PANEL_ENERGY_CAPACITY_TEXT, "  %s FE");
		add(KEY_WIDGET_PANEL_ENERGY_CRAFTING_TITLE, "Crafting");
		add(KEY_WIDGET_PANEL_ENERGY_CRAFTING_TEXT, "  %s t (%s s)");
		add(KEY_WIDGET_PANEL_UPGRADE, "Upgrades");
		add(KEY_WIDGET_PANEL_SETTINGS, "Settings");
		// Sounds
		add(KEY_SUBTITLE_MULTIBLOCK_FORM, "Multiblock forms");
		add(KEY_SUBTITLE_MULTIBLOCK_CRUSHER, "Crusher crushes");
	}
}
