package mwk.testmod;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your
// config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = TestMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TestModConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	// Machine config
	public static final ModConfigSpec.IntValue MACHINE_ENERGY_CAPACITY_DEFAULT =
			BUILDER.comment("The default energy capacity for machines")
					.defineInRange("machinePowerCapacityDefault", 50_000, 0, Integer.MAX_VALUE);
	public static final ModConfigSpec.IntValue MACHINE_ITEM_IO_SPEED_DEFAULT =
			BUILDER.comment("The default item transfer speed for machines")
					.defineInRange("itemIoSpeedDefault", 4, 0, Integer.MAX_VALUE);
	public static final ModConfigSpec.IntValue MACHINE_FLUID_IO_SPEED_DEFAULT =
			BUILDER.comment("The default fluid transfer speed for machines")
					.defineInRange("fluidIoSpeedDefault", 100, 0, Integer.MAX_VALUE);
	// Generator config
	public static final ModConfigSpec.IntValue GENERATOR_ENERGY_CAPACITY_DEFAULT =
			BUILDER.comment("The default energy capacity for generators")
					.defineInRange("generatorPowerCapacityDefault", 100_000, 0, Integer.MAX_VALUE);
	public static final ModConfigSpec.IntValue GENERATOR_REDSTONE_ENERGY_PER_TICK =
			BUILDER.comment("The default energy generated per tick for redstone generators")
					.defineInRange("generatorEnergyPerTickRedstone", 128, 0, Integer.MAX_VALUE);
	public static final ModConfigSpec.IntValue GENERATOR_GEOTHERMAL_ENERGY_PER_TICK =
			BUILDER.comment("The default energy generated per tick for geothermal generators")
					.defineInRange("generatorEnergyPerTickGeothermal", 128, 0, Integer.MAX_VALUE);
	public static final ModConfigSpec.IntValue GENERATOR_GEOTHERMAL_TANK_CAPACITY =
			BUILDER.comment("The default tank capacity for geothermal generators")
					.defineInRange("generatorTankCapacityGeothermal", 10_000, 0, Integer.MAX_VALUE);
	public static final ModConfigSpec.IntValue GENERATOR_STIRLING_ENERGY_PER_TICK =
			BUILDER.comment("The default energy generated per tick for stirling generators")
					.defineInRange("generatorEnergyPerTickStirling", 64, 0, Integer.MAX_VALUE);

	private static final ModConfigSpec.BooleanValue LOG_DIRT = BUILDER
			.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);

	private static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER.comment("A magic number")
			.defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

	public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION =
			BUILDER.comment("What you want the introduction message to be for the magic number")
					.define("magicNumberIntroduction", "The magic number is... ");

	// a list of strings that are treated as resource locations for items
	private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS =
			BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items",
					List.of("minecraft:iron_ingot"), TestModConfig::validateItemName);

	static final ModConfigSpec SPEC = BUILDER.build();

	public static boolean logDirtBlock;
	public static int magicNumber;
	public static String magicNumberIntroduction;
	public static Set<Item> items;

	private static boolean validateItemName(final Object obj) {
		return obj instanceof String itemName
				&& BuiltInRegistries.ITEM.containsKey(new ResourceLocation(itemName));
	}

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		logDirtBlock = LOG_DIRT.get();
		magicNumber = MAGIC_NUMBER.get();
		magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

		// convert the list of strings into a set of items
		items = ITEM_STRINGS.get().stream()
				.map(itemName -> BuiltInRegistries.ITEM.get(new ResourceLocation(itemName)))
				.collect(Collectors.toSet());
	}
}
