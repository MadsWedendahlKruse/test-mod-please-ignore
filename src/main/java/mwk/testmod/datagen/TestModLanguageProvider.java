package mwk.testmod.datagen;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.ConduitConnectionType;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class TestModLanguageProvider extends LanguageProvider {

    // Descriptions
    public static final String KEY_DESCRIPTION_MISSING = "description.testmod.missing";
    public static final String KEY_DESCRIPTION_SUPER_ASSEMBLER =
            "description.testmod.super_assembler";
    public static final String KEY_DESCRIPTION_INDUCTION_FURNACE =
            "description.testmod.induction_furnace";
    public static final String KEY_DESCRIPTION_CRUSHER = "description.testmod.crusher";
    public static final String KEY_DESCRIPTION_SEPARATOR = "description.testmod.separator";
    public static final String KEY_DESCRIPTION_STAMPING_PRESS = "description.testmod.stamping_press";
    public static final String KEY_DESCRIPTION_REDSTONE_GENERATOR =
            "description.testmod.redstone_generator";
    public static final String KEY_DESCRIPTION_GEOTHERMAL_GENERATOR =
            "description.testmod.geothermal_generator";
    public static final String KEY_DESCRIPTION_STIRLING_GENERATOR =
            "description.testmod.stirling_generator";
    public static final String KEY_DESCRIPTION_CAPACITRON = "description.testmod.capacitron";
    // Creative tabs
    public static final String KEY_CREATIVE_TAB = "itemGroup.testmod";
    // Info
    public static final String KEY_INFO_CONTROLLER_BLUEPRINT_HELP =
            "info.testmod.controller.blueprint.help";
    public static final String KEY_INFO_CONTROLLER_BLUEPRINT_COMPLETE =
            "info.testmod.controller.blueprint.complete";
    public static final String KEY_INFO_CONTROLLER_BLUEPRINT_INSUFFICIENT =
            "info.testmod.controller.blueprint.insufficient_blocks";
    public static final String KEY_INFO_CONTROLLER_BLUEPRINT_BLOCKED =
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
    public static final String KEY_INFO_CONDUIT_CYCLE_MODE = "info.testmod.conduit.cycle_mode";
    // Widgets
    public static final String KEY_WIDGET_HOLOGRAM_PROJECTOR_NONE =
            "widget.testmod.hologram_projector.none";
    public static final String KEY_WIDGET_HOLOGRAM_PROJECTOR_SEARCH =
            "widget.testmod.hologram_projector.search";
    public static final String KEY_WIDGET_HOLOGRAM_PROJECTOR_BLUEPRINTS =
            "widget.testmod.hologram_projector.blueprints";
    public static final String KEY_WIDGET_HOLOGRAM_PROJECTOR_BUTTONS =
            "widget.testmod.hologram_projector.buttons";
    public static final String KEY_WIDGET_ENERGY_BAR = "widget.testmod.energy_bar";
    public static final String KEY_WIDGET_ENERGY_BAR_TOOLTIP = "widget.testmod.energy_bar.tooltip";
    public static final String KEY_WIDGET_FLUID_BAR = "widget.testmod.fluid_bar";
    public static final String KEY_WIDGET_FLUID_BAR_TOOLTIP = "widget.testmod.fluid_bar.tooltip";
    public static final String KEY_WIDGET_FLUID_EMPTY = "widget.testmod.fluid.empty";
    public static final String KEY_WIDGET_PANEL_INFO = "widget.testmod.panel.info";
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
    public static final String KEY_WIDGET_AUTO_INSERT_TOOLTIP =
            "widget.testmod.auto_insert.tooltip";
    public static final String KEY_WIDGET_AUTO_EJECT_TOOLTIP = "widget.testmod.auto_eject.tooltip";
    public static final String KEY_WIDGET_TOOLTIP_ON = "widget.testmod.tooltip.on";
    public static final String KEY_WIDGET_TOOLTIP_OFF = "widget.testmod.tooltip.off";
    public static final String KEY_WIDGET_MISSING_STAMPING_DIE = "widget.testmod.missing_stamping_die";
    // TODO: Not sure where to put this
    public static final String KEY_WIDGET_GENERATOR_GENERATING_TITLE =
            "widget.testmod.generator.generating_title";
    public static final String KEY_WIDGET_GENERATOR_GENERATING_TEXT =
            "widget.testmod.generator.generating_text";
    public static final String KEY_WIDGET_GENERATOR_ENERGY_TITLE =
            "widget.testmod.generator.output_title";
    public static final String KEY_WIDGET_GENERATOR_ENERGY_TEXT =
            "widget.testmod.generator.output_text";
    public static final String KEY_WIDGET_GENERATOR_DURATION_TITLE =
            "widget.testmod.generator.duration_title";
    public static final String KEY_WIDGET_GENERATOR_DURATION_TEXT =
            "widget.testmod.generator.duration_text";
    // Sounds (subtitles)
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
        add(TestModBlocks.MACHINE_ITEM_INPUT_PORT.get(), "Machine Item Input Port");
        add(TestModBlocks.MACHINE_ITEM_OUTPUT_PORT.get(), "Machine Item Output Port");
        add(TestModBlocks.MACHINE_FLUID_INPUT_PORT.get(), "Machine Fluid Input Port");
        add(TestModBlocks.MACHINE_FLUID_OUTPUT_PORT.get(), "Machine Fluid Output Port");
        add(TestModBlocks.MACHINE_ENERGY_PORT.get(), "Machine Energy Port");
        add(TestModBlocks.COPPER_COIL.get(), "Copper Coil");
        add(TestModBlocks.ENERGY_CUBE.get(), "Energy Cube");
        add(TestModBlocks.SUPER_ASSEMBLER.get(), "Super Assembler");
        add(TestModBlocks.INDUCTION_FURNACE.get(), "Induction Furnace");
        add(TestModBlocks.CRUSHER.get(), "Crusher");
        add(TestModBlocks.SEPARATOR.get(), "Separator");
        add(TestModBlocks.STAMPING_PRESS.get(), "Stamping Press");
        add(TestModBlocks.REDSTONE_GENERATOR.get(), "Redstone Generator");
        add(TestModBlocks.GEOTHERMAL_GENERATOR.get(), "Geothermal Generator");
        add(TestModBlocks.STIRLING_GENERATOR.get(), "Stirling Generator");
        add(TestModBlocks.CAPACITRON.get(), "Capacitron");
        add(TestModBlocks.CONDUIT_ITEM.get(), "Item Conduit");
        add(TestModBlocks.CONDUIT_FLUID.get(), "Fluid Conduit");
        add(TestModBlocks.CONDUIT_ENERGY.get(), "Energy Conduit");
        // TEMPORARY DESCRIPTIONS
        add(KEY_DESCRIPTION_MISSING, "No description available.");
        add(KEY_DESCRIPTION_SUPER_ASSEMBLER,
                "A powerful machine designed to automate the crafting of complex items. It can store multiple recipes and automatically craft items when provided with the necessary materials.");
        add(KEY_DESCRIPTION_INDUCTION_FURNACE,
                "A high-temperature furnace capable of smelting ores and processing materials at a faster rate than a standard furnace. Essential for efficient ore processing and preparation in advanced manufacturing systems.");
        add(KEY_DESCRIPTION_CRUSHER,
                "A powerful machine designed to break down ores into finer materials, enabling the extraction of valuable resources. Essential for efficient ore processing and preparation in advanced manufacturing systems.");
        add(KEY_DESCRIPTION_SEPARATOR,
                "A machine designed to separate mixed materials into their individual components. Essential for efficient ore processing and preparation in advanced manufacturing systems.");
        add(KEY_DESCRIPTION_STAMPING_PRESS,
                "A machine designed to stamp and shape materials into specific forms. Essential for efficient manufacturing and production of advanced components.");
        add(KEY_DESCRIPTION_REDSTONE_GENERATOR,
                "A machine that generates energy from redstone. Useful for providing power to machines and systems in remote locations or where other power sources are unavailable.");
        add(KEY_DESCRIPTION_GEOTHERMAL_GENERATOR,
                "A machine that generates energy from the heat of lava. Useful for providing power to machines and systems in remote locations or where other power sources are unavailable.");
        add(KEY_DESCRIPTION_STIRLING_GENERATOR,
                "A machine that generates energy from solid fuels. Useful as an intial power source for the early stages of industrial development.");
        add(KEY_DESCRIPTION_CAPACITRON,
                "A machine that stores and provides energy to other machines. Useful for storing excess energy and providing power to machines and systems when needed.");
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
        add(TestModItems.IRON_PLATE.get(), "Iron Plate");
        add(TestModItems.COPPER_PLATE.get(), "Copper Plate");
        add(TestModItems.STEEL_PLATE.get(), "Steel Plate");
        add(TestModItems.IRON_GEAR.get(), "Iron Gear");
        add(TestModItems.RED_GIZMO.get(), "Red Gizmo");
        add(TestModItems.BLUE_GIZMO.get(), "Blue Gizmo");
        add(TestModItems.ENERGY_CELL.get(), "Energy Cell");
        add(TestModItems.SPEED_UPGRADE.get(), "Speed Upgrade");
        add(TestModItems.PLATE_STAMPING_DIE.get(), "Plate Stamping Die");
        add(TestModItems.GEAR_STAMPING_DIE.get(), "Gear Stamping Die");
        // Info
        add(KEY_INFO_CONTROLLER_BLUEPRINT_HELP, "Right-click with a wrench to see the blueprint");
        add(KEY_INFO_CONTROLLER_BLUEPRINT_COMPLETE,
                "The blueprint is complete, form the multiblock with a wrench");
        add(KEY_INFO_CONTROLLER_BLUEPRINT_INSUFFICIENT,
                "You are missing the required blocks to build the blueprint");
        add(KEY_INFO_CONTROLLER_BLUEPRINT_BLOCKED,
                "An incorrect block is in the way of the blueprint [x=%s, y=%s, z=%s]");
        add(KEY_INFO_CONTROLLER_MISSINGS, "Missing blocks:");
        add(KEY_INFO_CONTROLLER_INCORRECTS, "Incorrect blocks:");
        add(KEY_INFO_CONTROLLER_HOLOGRAM_COMPLETE, "Blueprint complete!");
        add(KEY_INFO_CONTROLLER_HOLOGRAM_RIGHT_CLICK_ME, "Right-click me with a wrench");
        add(KEY_INFO_HOLOGRAM_PROJECTOR_HELP, "Shift + right-click to choose a blueprint");
        add(KEY_INFO_HOLOGRAM_PROJECTOR_LOCKED, "Blueprint position locked");
        add(KEY_INFO_HOLOGRAM_PROJECTOR_UNLOCKED, "Blueprint position unlocked");
        add(KEY_INFO_CONDUIT_CYCLE_MODE, "Changed conduit mode to %s");
        // Widgets
        add(KEY_WIDGET_HOLOGRAM_PROJECTOR_NONE, "None");
        add(KEY_WIDGET_HOLOGRAM_PROJECTOR_SEARCH, "Search");
        add(KEY_WIDGET_HOLOGRAM_PROJECTOR_BLUEPRINTS, "Blueprints");
        add(KEY_WIDGET_HOLOGRAM_PROJECTOR_BUTTONS, "Buttons");
        add(KEY_WIDGET_ENERGY_BAR, "Energy");
        add(KEY_WIDGET_ENERGY_BAR_TOOLTIP, "%s/%s FE");
        add(KEY_WIDGET_FLUID_BAR, "Fluid");
        add(KEY_WIDGET_FLUID_BAR_TOOLTIP, "%s: %s/%s mB");
        add(KEY_WIDGET_FLUID_EMPTY, "Empty");
        add(KEY_WIDGET_PANEL_INFO, "Info");
        add(KEY_WIDGET_PANEL_ENERGY, "Energy");
        add(KEY_WIDGET_PANEL_ENERGY_USAGE_TITLE, "Usage");
        add(KEY_WIDGET_PANEL_ENERGY_USAGE_TEXT, "  %s FE/t (%s%%)");
        add(KEY_WIDGET_PANEL_ENERGY_CAPACITY_TITLE, "Capacity");
        add(KEY_WIDGET_PANEL_ENERGY_CAPACITY_TEXT, "  %s FE");
        add(KEY_WIDGET_PANEL_ENERGY_CRAFTING_TITLE, "Crafting");
        add(KEY_WIDGET_PANEL_ENERGY_CRAFTING_TEXT, "  %s t (%s s)");
        add(KEY_WIDGET_PANEL_UPGRADE, "Upgrades");
        add(KEY_WIDGET_PANEL_SETTINGS, "Settings");
        add(KEY_WIDGET_AUTO_INSERT_TOOLTIP, "Auto insert: %s");
        add(KEY_WIDGET_AUTO_EJECT_TOOLTIP, "Auto eject: %s");
        add(KEY_WIDGET_TOOLTIP_ON, "ON");
        add(KEY_WIDGET_TOOLTIP_OFF, "OFF");
        add(KEY_WIDGET_MISSING_STAMPING_DIE, "Missing stamping die");
        add(KEY_WIDGET_GENERATOR_ENERGY_TITLE, "Energy");
        add(KEY_WIDGET_GENERATOR_ENERGY_TEXT, "  %s FE");
        add(KEY_WIDGET_GENERATOR_GENERATING_TITLE, "Generating");
        add(KEY_WIDGET_GENERATOR_GENERATING_TEXT, "  %s FE/t");
        add(KEY_WIDGET_GENERATOR_DURATION_TITLE, "Duration");
        add(KEY_WIDGET_GENERATOR_DURATION_TEXT, "  %s");
        // Sounds (subtitles)
        add(KEY_SUBTITLE_MULTIBLOCK_FORM, "Multiblock forms");
        add(KEY_SUBTITLE_MULTIBLOCK_CRUSHER, "Crusher crushes");
        // Conduit connector types
        add(ConduitConnectionType.NONE.getSerializedName(), "None");
        add(ConduitConnectionType.CONDUIT.getSerializedName(), "Conduit");
        add(ConduitConnectionType.PULL.getSerializedName(), "Pull");
        add(ConduitConnectionType.PUSH.getSerializedName(), "Push");
        add(ConduitConnectionType.BIDIRECTIONAL.getSerializedName(), "Bidirectional");
    }
}
