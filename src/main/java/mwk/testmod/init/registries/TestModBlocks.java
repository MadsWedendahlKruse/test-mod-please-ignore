package mwk.testmod.init.registries;

import java.util.function.Supplier;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.ConduitBlock;
import mwk.testmod.common.block.conduit.ConduitType;
import mwk.testmod.common.block.entity.CrusherBlockEntity;
import mwk.testmod.common.block.entity.GeothermalGeneratorBlockEntity;
import mwk.testmod.common.block.entity.InductionFurnaceBlockEntity;
import mwk.testmod.common.block.entity.RedstoneGeneratorBlockEntity;
import mwk.testmod.common.block.entity.SeparatorBlockEntity;
import mwk.testmod.common.block.entity.StirlingGeneratorBlockEntity;
import mwk.testmod.common.block.entity.TeleporterBlockEntity;
import mwk.testmod.common.block.multiblock.HologramBlock;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockEnergyPortBlock;
import mwk.testmod.common.block.multiblock.MultiBlockIOPortBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.entity.MultiBlockFluidIOPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockItemIOPortBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModBlocks {

	private TestModBlocks() {}

	public static final DeferredRegister.Blocks BLOCKS =
			DeferredRegister.createBlocks(TestMod.MODID);

	public static final String ILMENITE_ORE_ID = "ilmenite_ore";
	public static final String DEEPSLATE_ILMENITE_ORE_ID = "deepslate_ilmenite_ore";

	public static final String MACHINE_FRAME_BASIC_ID = "machine_frame_basic";
	public static final String MACHINE_FRAME_REINFORCED_ID = "machine_frame_reinforced";
	public static final String MACHINE_FRAME_ADVANCED_ID = "machine_frame_advanced";
	public static final String MACHINE_ITEM_INPUT_PORT_ID = "machine_item_input_port";
	public static final String MACHINE_ITEM_OUTPUT_PORT_ID = "machine_item_output_port";
	public static final String MACHINE_FLUID_INPUT_PORT_ID = "machine_fluid_input_port";
	public static final String MACHINE_FLUID_OUTPUT_PORT_ID = "machine_fluid_output_port";
	public static final String MACHINE_ENERGY_PORT_ID = "machine_energy_port";
	public static final String COPPER_COIL_ID = "copper_coil";

	public static final String INDUCTION_FURNACE_ID = "induction_furnace";
	public static final String SUPER_ASSEMBLER_ID = "super_assembler";
	public static final String CRUSHER_ID = "crusher";
	public static final String SEPARATOR_ID = "separator";
	public static final String TELEPORTER_ID = "teleporter";

	public static final String REDSTONE_GENERATOR_ID = "redstone_generator";
	public static final String GEOTHERMAL_GENERATOR_ID = "geothermal_generator";
	public static final String STIRLING_GENERATOR_ID = "stirling_generator";

	public static final String HOLOGRAM_ID = "hologram";

	public static final String CONDUIT_ITEM_ID = "conduit_item";
	public static final String CONDUIT_FLUID_ID = "conduit_fluid";
	public static final String CONDUIT_ENERGY_ID = "conduit_energy";

	// Normal blocks
	public static final DeferredBlock<Block> ILMENITE_ORE =
			registerBlockWithItem(ILMENITE_ORE_ID, () -> new Block(Blocks.IRON_ORE.properties()));
	public static final DeferredBlock<Block> DEEPSLATE_ILMENITE_ORE = registerBlockWithItem(
			DEEPSLATE_ILMENITE_ORE_ID, () -> new Block(Blocks.DEEPSLATE_IRON_ORE.properties()));

	// Multiblock parts
	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_BASIC =
			registerBlockWithItem(MACHINE_FRAME_BASIC_ID,
					() -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_REINFORCED =
			registerBlockWithItem(MACHINE_FRAME_REINFORCED_ID,
					() -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_ADVANCED =
			registerBlockWithItem(MACHINE_FRAME_ADVANCED_ID,
					() -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockIOPortBlock> MACHINE_ITEM_INPUT_PORT =
			registerBlockWithItem(MACHINE_ITEM_INPUT_PORT_ID,
					() -> new MultiBlockIOPortBlock(getMachineProperties(), true,
							MultiBlockItemIOPortBlockEntity::new));
	public static final DeferredBlock<MultiBlockIOPortBlock> MACHINE_ITEM_OUTPUT_PORT =
			registerBlockWithItem(MACHINE_ITEM_OUTPUT_PORT_ID,
					() -> new MultiBlockIOPortBlock(getMachineProperties(), false,
							MultiBlockItemIOPortBlockEntity::new));
	public static final DeferredBlock<MultiBlockIOPortBlock> MACHINE_FLUID_INPUT_PORT =
			registerBlockWithItem(MACHINE_FLUID_INPUT_PORT_ID,
					() -> new MultiBlockIOPortBlock(getMachineProperties(), true,
							MultiBlockFluidIOPortBlockEntity::new));
	public static final DeferredBlock<MultiBlockIOPortBlock> MACHINE_FLUID_OUTPUT_PORT =
			registerBlockWithItem(MACHINE_FLUID_OUTPUT_PORT_ID,
					() -> new MultiBlockIOPortBlock(getMachineProperties(), false,
							MultiBlockFluidIOPortBlockEntity::new));
	public static final DeferredBlock<MultiBlockEnergyPortBlock> MACHINE_ENERGY_PORT =
			registerBlockWithItem(MACHINE_ENERGY_PORT_ID,
					() -> new MultiBlockEnergyPortBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockPartBlock> COPPER_COIL = registerBlockWithItem(
			COPPER_COIL_ID, () -> new MultiBlockPartBlock(getMachineProperties()));

	// --- Multiblock controllers ---
	// Crafters
	public static final DeferredBlock<MultiBlockControllerBlock> INDUCTION_FURNACE =
			registerBlockWithItem(INDUCTION_FURNACE_ID,
					() -> new MultiBlockControllerBlock(getMachineProperties(),
							InductionFurnaceBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> SUPER_ASSEMBLER =
			registerBlockWithItem(SUPER_ASSEMBLER_ID,
					() -> new MultiBlockControllerBlock(getMachineProperties(),
							// TODO: Replace with the actual block entity
							InductionFurnaceBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> CRUSHER = registerBlockWithItem(
			CRUSHER_ID,
			() -> new MultiBlockControllerBlock(getMachineProperties(), CrusherBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> SEPARATOR = registerBlockWithItem(
			SEPARATOR_ID,
			() -> new MultiBlockControllerBlock(getMachineProperties(), SeparatorBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> TELEPORTER = registerBlockWithItem(
			TELEPORTER_ID, () -> new MultiBlockControllerBlock(getMachineProperties(),
					TeleporterBlockEntity::new));

	// Generators
	public static final DeferredBlock<MultiBlockControllerBlock> REDSTONE_GENERATOR =
			registerBlockWithItem(REDSTONE_GENERATOR_ID,
					() -> new MultiBlockControllerBlock(getMachineProperties(),
							RedstoneGeneratorBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> GEOTHERMAL_GENERATOR =
			registerBlockWithItem(GEOTHERMAL_GENERATOR_ID,
					() -> new MultiBlockControllerBlock(getMachineProperties(),
							GeothermalGeneratorBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> STIRLING_GENERATOR =
			registerBlockWithItem(STIRLING_GENERATOR_ID,
					() -> new MultiBlockControllerBlock(getMachineProperties(),
							StirlingGeneratorBlockEntity::new));
	// ---

	public static final DeferredBlock<HologramBlock> HOLOGRAM =
			BLOCKS.register(HOLOGRAM_ID, () -> new HologramBlock());

	// Conduits
	public static final DeferredBlock<ConduitBlock> CONDUIT_ITEM =
			registerBlockWithItem(CONDUIT_ITEM_ID,
					() -> new ConduitBlock(BlockBehaviour.Properties.of(), ConduitType.ITEM));
	public static final DeferredBlock<ConduitBlock> CONDUIT_FLUID =
			registerBlockWithItem(CONDUIT_FLUID_ID,
					() -> new ConduitBlock(BlockBehaviour.Properties.of(), ConduitType.FLUID));
	public static final DeferredBlock<ConduitBlock> CONDUIT_ENERGY =
			registerBlockWithItem(CONDUIT_ENERGY_ID,
					() -> new ConduitBlock(BlockBehaviour.Properties.of(), ConduitType.ENERGY));

	public static BlockBehaviour.Properties getMachineProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
				// TODO: What do these numbers mean?
				.strength(3.0F, 6.0F).requiresCorrectToolForDrops();
	}

	public static <T extends Block> DeferredBlock<T> registerBlockWithItem(String id,
			Supplier<T> blockSupplier) {
		DeferredBlock<T> block = BLOCKS.register(id, blockSupplier);
		TestModItems.ITEMS.registerSimpleBlockItem(id, block);
		return block;
	}

	public static void register(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
	}
}
