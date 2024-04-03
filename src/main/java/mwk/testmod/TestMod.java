package mwk.testmod;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import mwk.testmod.client.gui.screen.CrusherScreen;
import mwk.testmod.client.gui.screen.InductionFurnaceScreen;
import mwk.testmod.client.gui.screen.SeparatorScreen;
import mwk.testmod.client.render.block_entity.CrusherBlockEntityRenderer;
import mwk.testmod.client.render.block_entity.SeparatorBlockEntityRenderer;
import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.common.block.multiblock.HologramBlockColor;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModCreativeTabs;
import mwk.testmod.init.registries.TestModItems;
import mwk.testmod.init.registries.TestModMenus;
import mwk.testmod.init.registries.TestModModels;
import mwk.testmod.init.registries.TestModRecipeSerializers;
import mwk.testmod.init.registries.TestModRecipeTypes;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
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
		TestModMenus.register(modEventBus);
		TestModRecipeTypes.register(modEventBus);
		TestModRecipeSerializers.register(modEventBus);

		// Register ourselves for server and other game events we are interested in.
		// Note that this is necessary if and only if we want *this* class (testmod) to
		// respond directly to events. Do not add this line if there are no
		// @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
		NeoForge.EVENT_BUS.register(this);

		// Register the item to a creative tab
		modEventBus.addListener(this::addCreative);
		modEventBus.addListener(this::registerCapabilities);

		// Register our mod's ModConfigSpec so that FML can create and load the config file
		// for us
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TestModConfig.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Some common setup code
		LOGGER.info("HELLO FROM COMMON SETUP");

		if (TestModConfig.logDirtBlock)
			LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

		LOGGER.info(TestModConfig.magicNumberIntroduction + TestModConfig.magicNumber);

		TestModConfig.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		TestModCreativeTabs.addToTabs(event);
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		// TODO: This seems like a quite cumbersome way to register capabilities
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
				TestModBlockEntities.MULTI_ENERGY_PORT_ENTITY_TYPE.get(),
				(entity, direction) -> entity.getEnergyHandler(direction));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
				TestModBlockEntities.MULTI_INPUT_PORT_ENTITY_TYPE.get(),
				(entity, direction) -> entity.getItemHandler(direction));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
				TestModBlockEntities.MULTI_OUTPUT_PORT_ENTITY_TYPE.get(),
				(entity, direction) -> entity.getItemHandler(direction));

		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
				TestModBlockEntities.INDUCTION_FURNACE_ENTITY_TYPE.get(),
				(entity, direction) -> entity.getEnergyHandler(direction));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
				TestModBlockEntities.INDUCTION_FURNACE_ENTITY_TYPE.get(),
				(entity, direction) -> entity.getItemHandler(direction));
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
				TestModBlockEntities.CRUSHER_ENTITY_TYPE.get(),
				(entity, direction) -> entity.getEnergyHandler(direction));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
				TestModBlockEntities.CRUSHER_ENTITY_TYPE.get(),
				(entity, direction) -> entity.getItemHandler(direction));
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		// Do something when the server starts
		LOGGER.info("HELLO from server starting");
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

			// TODO: McJty's tutorial uses enqueueWork here, but I don't know why
			event.enqueueWork(() -> {
				MenuScreens.register(TestModMenus.INDUCTION_FURNACE_MENU.get(),
						InductionFurnaceScreen::new);
				MenuScreens.register(TestModMenus.CRUSHER_MENU.get(), CrusherScreen::new);
				MenuScreens.register(TestModMenus.SEPARATOR_MENU.get(), SeparatorScreen::new);
			});
		}

		@SubscribeEvent
		public static void onRegisterColorHandlersEvent(RegisterColorHandlersEvent.Block event) {
			event.register(new HologramBlockColor(), TestModBlocks.HOLOGRAM.get());
		}

		@SubscribeEvent
		public static void onRegisterRenderersEvent(EntityRenderersEvent.RegisterRenderers event) {
			event.registerBlockEntityRenderer(TestModBlockEntities.CRUSHER_ENTITY_TYPE.get(),
					(context) -> new CrusherBlockEntityRenderer(context));
			event.registerBlockEntityRenderer(TestModBlockEntities.SEPARATOR_ENTITY_TYPE.get(),
					(context) -> new SeparatorBlockEntityRenderer(context));
		}

		@SubscribeEvent
		public static void onRegisterAdditionalEvent(ModelEvent.RegisterAdditional event) {
			TestModModels.register(event);
		}
	}
}
