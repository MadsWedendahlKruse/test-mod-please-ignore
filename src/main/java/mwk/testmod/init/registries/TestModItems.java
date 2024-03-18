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

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TestMod.MODID);

	// Items
	public static final DeferredItem<WrenchItem> WRENCH_ITEM =
			ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties()));
	public static final DeferredItem<HologramProjectorItem> HOLOGRAM_PROJECTOR_ITEM = ITEMS
			.register("hologram_projector", () -> new HologramProjectorItem(new Item.Properties()));
	// Block items (mutliblock parts)
	public static final DeferredItem<BlockItem> MACHINE_FRAME_BASIC_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.MACHINE_FRAME_BASIC_ID,
					TestModBlocks.MACHINE_FRAME_BASIC_BLOCK);
	public static final DeferredItem<BlockItem> MACHINE_FRAME_REINFORCED_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.MACHINE_FRAME_REINFORCED_ID,
					TestModBlocks.MACHINE_FRAME_REINFORCED_BLOCK);
	public static final DeferredItem<BlockItem> MACHINE_FRAME_ADVANCED_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.MACHINE_FRAME_ADVANCED_ID,
					TestModBlocks.MACHINE_FRAME_ADVANCED_BLOCK);
	public static final DeferredItem<BlockItem> MACHINE_IO_PORT_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.MACHINE_INPUT_PORT_ID,
					TestModBlocks.MACHINE_INPUT_PORT_BLOCK);
	public static final DeferredItem<BlockItem> MACHINE_OUTPUT_PORT_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.MACHINE_OUTPUT_PORT_ID,
					TestModBlocks.MACHINE_OUTPUT_PORT_BLOCK);
	public static final DeferredItem<BlockItem> MACHINE_POWER_PORT_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.MACHINE_ENERGY_PORT_ID,
					TestModBlocks.MACHINE_ENERGY_PORT_BLOCK);
	// Block items (multiblock controllers)
	public static final DeferredItem<BlockItem> SUPER_FURNACE_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.SUPER_FURNACE_ID,
					TestModBlocks.SUPER_FURNACE_BLOCK);
	public static final DeferredItem<BlockItem> SUPER_ASSEMBLER_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.SUPER_ASSEMBLER_ID,
					TestModBlocks.SUPER_ASSEMBLER_BLOCK);
	public static final DeferredItem<BlockItem> CRUSHER_BLOCK_ITEM =
			ITEMS.registerSimpleBlockItem(TestModBlocks.CRUSHER_ID, TestModBlocks.CRUSHER_BLOCK);

	public static void register(IEventBus modEventBus) {
		ITEMS.register(modEventBus);
	}
}

