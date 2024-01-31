package mwk.testmod;

import com.google.common.base.Predicate;
import com.mojang.logging.LogUtils;

import mwk.testmod.common.block.frame.MachineFrameBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlockEntity;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintRegistry;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.block.multiblock.controller.MultiBlockControllerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TestMod.MODID)
public class TestMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "testmod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "testmod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold BlockEntityTypes which will all be registered under the "testmod" namespace
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "testmod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "testmod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    // Create a registry for multiblock blueprints
    // TODO: Not sure if I need to do something more fancy here?
    public static final BlueprintRegistry BLUEPRINT_REGISTRY = BlueprintRegistry.getInstance();
    
    // Creates a new Block with the id "testmod:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredBlock<MachineFrameBlock> MACHINE_FRAME_BASIC_BLOCK = BLOCKS.register(
        "machine_frame_basic", () -> new MachineFrameBlock(BlockBehaviour.Properties.of())); 
    public static final DeferredBlock<MachineFrameBlock> MACHINE_FRAME_REINFORCED_BLOCK = BLOCKS.register(
        "machine_frame_reinforced", () -> new MachineFrameBlock(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<MachineFrameBlock> MACHINE_FRAME_ADVANCED_BLOCK = BLOCKS.register(
        "machine_frame_advanced", () -> new MachineFrameBlock(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<MultiBlockControllerBlock> SUPER_FURNACE_BLOCK = BLOCKS.register(
        "super_furnace", () -> new MultiBlockControllerBlock(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<MultiBlockControllerBlock> SUPER_ASSEMBLER_BLOCK = BLOCKS.register(
        "super_assembler", () -> new MultiBlockControllerBlock(BlockBehaviour.Properties.of()));

    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MultiBlockPartBlockEntity>> MULTI_BLOCK_PART_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register(
        "multi_block_part", () -> BlockEntityType.Builder.of(MultiBlockPartBlockEntity::new, MACHINE_FRAME_BASIC_BLOCK.get()).build(null));

    // Creates a new BlockItem with the id "testmod:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
    public static final DeferredItem<BlockItem> MACHINE_FRAME_BASIC_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
        "machine_frame_basic", MACHINE_FRAME_BASIC_BLOCK);
    public static final DeferredItem<BlockItem> MACHINE_FRAME_REINFORCED_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
        "machine_frame_reinforced", MACHINE_FRAME_REINFORCED_BLOCK);
    public static final DeferredItem<BlockItem> MACHINE_FRAME_ADVANCED_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
        "machine_frame_advanced", MACHINE_FRAME_ADVANCED_BLOCK);
    public static final DeferredItem<BlockItem> SUPER_FURNACE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
        "super_furnace", SUPER_FURNACE_BLOCK);
    public static final DeferredItem<BlockItem> SUPER_ASSEMBLER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(
        "super_assembler", SUPER_ASSEMBLER_BLOCK);

    // Creates a new food item with the id "testmod:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build()));

    // Creates a creative tab with the id "testmod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.testmod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(MACHINE_FRAME_BASIC_BLOCK_ITEM.get());
                output.accept(MACHINE_FRAME_REINFORCED_BLOCK_ITEM.get());
                output.accept(MACHINE_FRAME_ADVANCED_BLOCK_ITEM.get());
                output.accept(SUPER_FURNACE_BLOCK_ITEM.get());
                output.accept(SUPER_ASSEMBLER_BLOCK_ITEM.get());
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public TestMod(IEventBus modEventBus)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so block entity types get registered
        BLOCK_ENTITY_TYPES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (testmod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Load the blueprints
        BLUEPRINT_REGISTRY.load();

        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
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
                LOGGER.info("Loading blueprint: {}", location);
                BLUEPRINT_REGISTRY.registerBlueprint(location.getPath(), 
                    MultiBlockBlueprint.create(resourceManager, location));
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
