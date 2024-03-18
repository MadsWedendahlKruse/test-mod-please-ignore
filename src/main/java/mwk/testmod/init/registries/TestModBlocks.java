package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.CrusherBlockEntity;
import mwk.testmod.common.block.entity.SuperFurnaceBlockEntity;
import mwk.testmod.common.block.multiblock.HologramBlock;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockEnergyPortBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.MutliBlockIOPortBlock;
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

	public static final String MACHINE_FRAME_BASIC_ID = "machine_frame_basic";
	public static final String MACHINE_FRAME_REINFORCED_ID = "machine_frame_reinforced";
	public static final String MACHINE_FRAME_ADVANCED_ID = "machine_frame_advanced";
	public static final String MACHINE_INPUT_PORT_ID = "machine_input_port";
	public static final String MACHINE_OUTPUT_PORT_ID = "machine_output_port";
	public static final String MACHINE_ENERGY_PORT_ID = "machine_energy_port";

	public static final String SUPER_FURNACE_ID = "super_furnace";
	public static final String SUPER_ASSEMBLER_ID = "super_assembler";
	public static final String CRUSHER_ID = "crusher";

	public static final String HOLOGRAM_ID = "hologram";

	// Multiblock parts
	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_BASIC_BLOCK =
			BLOCKS.register(MACHINE_FRAME_BASIC_ID,
					() -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_REINFORCED_BLOCK =
			BLOCKS.register(MACHINE_FRAME_REINFORCED_ID,
					() -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_ADVANCED_BLOCK =
			BLOCKS.register(MACHINE_FRAME_ADVANCED_ID,
					() -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MutliBlockIOPortBlock> MACHINE_INPUT_PORT_BLOCK =
			BLOCKS.register(MACHINE_INPUT_PORT_ID,
					() -> new MutliBlockIOPortBlock(getMachineProperties(), true));
	public static final DeferredBlock<MutliBlockIOPortBlock> MACHINE_OUTPUT_PORT_BLOCK =
			BLOCKS.register(MACHINE_OUTPUT_PORT_ID,
					() -> new MutliBlockIOPortBlock(getMachineProperties(), false));
	public static final DeferredBlock<MultiBlockEnergyPortBlock> MACHINE_ENERGY_PORT_BLOCK =
			BLOCKS.register(MACHINE_ENERGY_PORT_ID,
					() -> new MultiBlockEnergyPortBlock(getMachineProperties()));

	// Multiblock controllers
	public static final DeferredBlock<MultiBlockControllerBlock> SUPER_FURNACE_BLOCK = BLOCKS
			.register(SUPER_FURNACE_ID, () -> new MultiBlockControllerBlock(getMachineProperties(),
					SuperFurnaceBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> SUPER_ASSEMBLER_BLOCK =
			BLOCKS.register(SUPER_ASSEMBLER_ID,
					() -> new MultiBlockControllerBlock(getMachineProperties(),
							// TODO: Replace with the actual block entity
							SuperFurnaceBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> CRUSHER_BLOCK = BLOCKS.register(
			CRUSHER_ID,
			() -> new MultiBlockControllerBlock(getMachineProperties(), CrusherBlockEntity::new));

	public static final DeferredBlock<HologramBlock> HOLOGRAM_BLOCK =
			BLOCKS.register(HOLOGRAM_ID, () -> new HologramBlock());

	public static void register(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
	}

	public static BlockBehaviour.Properties getMachineProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
				// TODO: What do these numbers mean?
				.strength(3.0F, 6.0F).requiresCorrectToolForDrops();
	}
}
