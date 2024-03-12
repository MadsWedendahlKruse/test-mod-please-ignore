package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.SuperFurnaceBlockEntity;
import mwk.testmod.common.block.multiblock.HologramBlock;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
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

	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_BASIC_BLOCK = BLOCKS
			.register("machine_frame_basic", () -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_REINFORCED_BLOCK =
			BLOCKS.register("machine_frame_reinforced",
					() -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_ADVANCED_BLOCK =
			BLOCKS.register("machine_frame_advanced",
					() -> new MultiBlockPartBlock(getMachineProperties()));
	public static final DeferredBlock<MultiBlockControllerBlock> SUPER_FURNACE_BLOCK = BLOCKS
			.register("super_furnace", () -> new MultiBlockControllerBlock(getMachineProperties(),
					SuperFurnaceBlockEntity::new));
	public static final DeferredBlock<MultiBlockControllerBlock> SUPER_ASSEMBLER_BLOCK =
			BLOCKS.register("super_assembler",
					() -> new MultiBlockControllerBlock(getMachineProperties(),
							// TODO: Replace with the actual block entity
							SuperFurnaceBlockEntity::new));

	public static final DeferredBlock<HologramBlock> HOLOGRAM_BLOCK =
			BLOCKS.register("hologram", () -> new HologramBlock());

	public static void register(IEventBus modEventBus) {
		BLOCKS.register(modEventBus);
	}

	public static BlockBehaviour.Properties getMachineProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL)
				// TODO: What do these numbers mean?
				.strength(3.0F, 6.0F).requiresCorrectToolForDrops();
	}
}
