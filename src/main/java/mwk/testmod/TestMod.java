package mwk.testmod;

import java.util.Map;
import org.slf4j.Logger;
import com.google.common.base.Predicate;
import com.mojang.logging.LogUtils;
import mwk.testmod.client.hologram.HologramRenderer;
import mwk.testmod.common.block.multiblock.HologramBlockColor;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintRegistry;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModCreativeTabs;
import mwk.testmod.init.registries.TestModItems;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TestMod.MODID)
public class TestMod {
	// Define mod id in a common place for everything to reference
	public static final String MODID = "testmod";
	// Directly reference a slf4j logger
	// private static final Logger LOGGER = LogUtils.getLogger();
	public static final Logger LOGGER = LogUtils.getLogger();

	// TODO: Not sure if I need to do something more fancy here?
	public static final BlueprintRegistry BLUEPRINT_REGISTRY = BlueprintRegistry.getInstance();

	// The constructor for the mod class is the first code that is run when your mod is loaded.
	// FML will recognize some parameter types like IEventBus or ModContainer and pass them in
	// automatically.
	public TestMod(IEventBus modEventBus) {
		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);

		TestModBlocks.register(modEventBus);
		TestModBlockEntities.register(modEventBus);
		TestModItems.register(modEventBus);
		TestModCreativeTabs.register(modEventBus);
		TestModSounds.register(modEventBus);

		// Register ourselves for server and other game events we are interested in.
		// Note that this is necessary if and only if we want *this* class (testmod) to
		// respond directly to events. Do not add this line if there are no
		// @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
		NeoForge.EVENT_BUS.register(this);

		// Register the item to a creative tab
		modEventBus.addListener(this::addCreative);

		// Register our mod's ModConfigSpec so that FML can create and load the config file
		// for us
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Some common setup code
		LOGGER.info("HELLO FROM COMMON SETUP");

		if (Config.logDirtBlock)
			LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

		LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

		Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
	}

	// Add the example block item to the building blocks tab
	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			// event.accept(EXAMPLE_BLOCK_ITEM);
		}
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		// Do something when the server starts
		LOGGER.info("HELLO from server starting");

		// Load multiblock blueprints
		// TODO: Can't these be loaded when the game starts?
		String path = "blueprints";
		Predicate<ResourceLocation> jsonFilter = s -> s.getPath().endsWith(".json");
		ResourceManager resourceManager = event.getServer().getResourceManager();
		Map<ResourceLocation, Resource> resources = resourceManager.listResources(path, jsonFilter);
		for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
			ResourceLocation location = entry.getKey();
			if (location.getNamespace().equals(MODID)) {
				LOGGER.info("Loading blueprint from: {}", location);
				MultiBlockBlueprint blueprint =
						MultiBlockBlueprint.create(resourceManager, location);
				BLUEPRINT_REGISTRY.registerBlueprint(blueprint);
				LOGGER.info("Loaded blueprint with name: {}", blueprint.getName());
			}
		}
	}

	// You can use EventBusSubscriber to automatically register all static methods in the class
	// annotated with @SubscribeEvent
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD,
			value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			// Some client setup code
			LOGGER.info("HELLO FROM CLIENT SETUP");
			LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
			HologramRenderer.getInstance().init();
		}

		@SubscribeEvent
		public static void onRegisterColorHandlersEvent(RegisterColorHandlersEvent.Block event) {
			event.register(new HologramBlockColor(), TestModBlocks.HOLOGRAM_BLOCK.get());
		}
	}

	// @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE,
	// value = Dist.CLIENT)
	// public static class ClientForgeEvents {

	// }
}
