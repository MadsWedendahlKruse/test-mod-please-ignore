package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.item.HologramProjectorItem;
import mwk.testmod.common.item.WrenchItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModItems {

        private TestModItems() {}

        public static final DeferredRegister.Items ITEMS =
                        DeferredRegister.createItems(TestMod.MODID);

        // Items
        public static final DeferredItem<WrenchItem> WRENCH_ITEM =
                        ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties()));
        public static final DeferredItem<HologramProjectorItem> HOLOGRAM_PROJECTOR_ITEM =
                        ITEMS.register("hologram_projector",
                                        () -> new HologramProjectorItem(new Item.Properties()));
        // Block items (mutliblock parts)
        public static final DeferredItem<BlockItem> MACHINE_FRAME_BASIC_BLOCK_ITEM =
                        ITEMS.registerSimpleBlockItem("machine_frame_basic",
                                        TestModBlocks.MACHINE_FRAME_BASIC_BLOCK);
        public static final DeferredItem<BlockItem> MACHINE_FRAME_REINFORCED_BLOCK_ITEM =
                        ITEMS.registerSimpleBlockItem("machine_frame_reinforced",
                                        TestModBlocks.MACHINE_FRAME_REINFORCED_BLOCK);
        public static final DeferredItem<BlockItem> MACHINE_FRAME_ADVANCED_BLOCK_ITEM =
                        ITEMS.registerSimpleBlockItem("machine_frame_advanced",
                                        TestModBlocks.MACHINE_FRAME_ADVANCED_BLOCK);
        public static final DeferredItem<BlockItem> MACHINE_IO_PORT_BLOCK_ITEM =
                        ITEMS.registerSimpleBlockItem("machine_io_port",
                                        TestModBlocks.MACHINE_IO_PORT_BLOCK);
        public static final DeferredItem<BlockItem> MACHINE_POWER_PORT_BLOCK_ITEM =
                        ITEMS.registerSimpleBlockItem("machine_power_port",
                                        TestModBlocks.MACHINE_POWER_PORT_BLOCK);
        // Block items (multiblock controllers)
        public static final DeferredItem<BlockItem> SUPER_FURNACE_BLOCK_ITEM =
                        ITEMS.registerSimpleBlockItem("super_furnace",
                                        TestModBlocks.SUPER_FURNACE_BLOCK);
        public static final DeferredItem<BlockItem> SUPER_ASSEMBLER_BLOCK_ITEM =
                        ITEMS.registerSimpleBlockItem("super_assembler",
                                        TestModBlocks.SUPER_ASSEMBLER_BLOCK);
        public static final DeferredItem<BlockItem> CRUSHER_BLOCK_ITEM =
                        ITEMS.registerSimpleBlockItem("crusher", TestModBlocks.CRUSHER_BLOCK);

        public static void register(IEventBus modEventBus) {
                ITEMS.register(modEventBus);
        }
}

