package mwk.testmod;

import com.mojang.logging.LogUtils;
import mwk.testmod.client.gui.screen.CapacitronScreen;
import mwk.testmod.client.gui.screen.CrusherScreen;
import mwk.testmod.client.gui.screen.GeothermalGeneratorScreen;
import mwk.testmod.client.gui.screen.InductionFurnaceScreen;
import mwk.testmod.client.gui.screen.RedstoneGeneratorScreen;
import mwk.testmod.client.gui.screen.SeparatorScreen;
import mwk.testmod.client.gui.screen.StampingPressScreen;
import mwk.testmod.client.gui.screen.StirlingGeneratorScreen;
import mwk.testmod.client.render.block_entity.CrusherBlockEntityRenderer;
import mwk.testmod.client.render.block_entity.SeparatorBlockEntityRenderer;
import mwk.testmod.client.render.block_entity.StampingPressBlockEntityRenderer;
import mwk.testmod.client.render.block_entity.StirlingGeneratorBlockEntityRenderer;
import mwk.testmod.client.render.conduit.FluidConduitBlockEntityRenderer;
import mwk.testmod.client.render.hologram.HologramRenderer;
import mwk.testmod.common.block.conduit.ConduitBlockEntity;
import mwk.testmod.common.block.multiblock.HologramBlockColor;
import mwk.testmod.common.block.multiblock.entity.MultiBlockEnergyPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockFluidIOPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockItemIOPortBlockEntity;
import mwk.testmod.common.network.BuildMultiBlockPacket;
import mwk.testmod.common.network.MachineIOPacket;
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
import net.minecraft.server.MinecraftServer;
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
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.slf4j.Logger;

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

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::onRegisterPayloadHandlers);

        // Register our mod's ModConfigSpec so that FML can create and load the config file
        // for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TestModConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (TestModConfig.logDirtBlock) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info(TestModConfig.magicNumberIntroduction + TestModConfig.magicNumber);

        TestModConfig.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        TestModCreativeTabs.addToTabs(event);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                TestModBlockEntities.MULTI_ENERGY_PORT_ENTITY_TYPE.get(),
                MultiBlockEnergyPortBlockEntity::getEnergyHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                TestModBlockEntities.MULTI_ITEM_INPUT_PORT_ENTITY_TYPE.get(),
                MultiBlockItemIOPortBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                TestModBlockEntities.MULTI_ITEM_OUTPUT_PORT_ENTITY_TYPE.get(),
                MultiBlockItemIOPortBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                TestModBlockEntities.MULTI_FLUID_INPUT_PORT_ENTITY_TYPE.get(),
                MultiBlockFluidIOPortBlockEntity::getFluidHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                TestModBlockEntities.MULTI_FLUID_OUTPUT_PORT_ENTITY_TYPE.get(),
                MultiBlockFluidIOPortBlockEntity::getFluidHandler);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                TestModBlockEntities.CONDUIT_ITEM_ENTITY_TYPE.get(),
                ConduitBlockEntity::getCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                TestModBlockEntities.CONDUIT_FLUID_ENTITY_TYPE.get(),
                ConduitBlockEntity::getCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                TestModBlockEntities.CONDUIT_ENERGY_ENTITY_TYPE.get(),
                ConduitBlockEntity::getCapability);

    }

    private void onRegisterPayloadHandlers(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(MODID);
        registrar.play(MachineIOPacket.ID, MachineIOPacket::new,
                handler -> handler.server(MachineIOPacket::handleServer));
        registrar.play(BuildMultiBlockPacket.ID, BuildMultiBlockPacket::new,
                handler -> handler.server(BuildMultiBlockPacket::handleServer));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
        MinecraftServer server = event.getServer();
        server.getAllLevels().forEach((level) -> LOGGER.info("LEVEL >> {}", level));
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
                MenuScreens.register(TestModMenus.STAMPING_PRESS_MENU.get(),
                        StampingPressScreen::new);

                MenuScreens.register(TestModMenus.REDSTONE_GENERATOR_MENU.get(),
                        RedstoneGeneratorScreen::new);
                MenuScreens.register(TestModMenus.GEOTHERMAL_GENERATOR_MENU.get(),
                        GeothermalGeneratorScreen::new);
                MenuScreens.register(TestModMenus.STIRLING_GENERATOR_MENU.get(),
                        StirlingGeneratorScreen::new);

                MenuScreens.register(TestModMenus.CAPACITRON_MENU.get(), CapacitronScreen::new);
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
            event.registerBlockEntityRenderer(
                    TestModBlockEntities.STIRLING_GENERATOR_ENTITY_TYPE.get(),
                    (context) -> new StirlingGeneratorBlockEntityRenderer(context));
            event.registerBlockEntityRenderer(TestModBlockEntities.STAMPING_PRESS_ENTITY_TYPE.get(),
                    (context) -> new StampingPressBlockEntityRenderer(context));

            event.registerBlockEntityRenderer(TestModBlockEntities.CONDUIT_FLUID_ENTITY_TYPE.get(),
                    (context) -> new FluidConduitBlockEntityRenderer(context));
        }

        @SubscribeEvent
        public static void onRegisterAdditionalEvent(ModelEvent.RegisterAdditional event) {
            TestModModels.register(event);
        }
    }
}
