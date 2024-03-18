package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.CrusherBlockEntity;
import mwk.testmod.common.block.entity.SuperFurnaceBlockEntity;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.entity.MultiBlockEnergyPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockPartBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MutliBlockIOPortBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModBlockEntities {

	private TestModBlockEntities() {}

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
			DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TestMod.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockPartBlockEntity>> MULTI_BLOCK_PART_BLOCK_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register("multi_block_part",
					() -> BlockEntityType.Builder.of(MultiBlockPartBlockEntity::new,
							TestModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get)
									.filter(block -> block instanceof MultiBlockPartBlock)
									.toArray(Block[]::new))
							.build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockEnergyPortBlockEntity>> MULTI_BLOCK_ENERGY_PORT_BLOCK_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES
					.register(TestModBlocks.MACHINE_ENERGY_PORT_ID,
							() -> BlockEntityType.Builder
									.of(MultiBlockEnergyPortBlockEntity::new,
											TestModBlocks.MACHINE_ENERGY_PORT_BLOCK.get())
									.build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MutliBlockIOPortBlockEntity>> MULTI_BLOCK_INPUT_PORT_BLOCK_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.MACHINE_INPUT_PORT_ID,
					() -> BlockEntityType.Builder
							.of((blockPos, blockState) -> new MutliBlockIOPortBlockEntity(blockPos,
									blockState, true), TestModBlocks.MACHINE_INPUT_PORT_BLOCK.get())
							.build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MutliBlockIOPortBlockEntity>> MULTI_BLOCK_OUTPUT_PORT_BLOCK_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.MACHINE_OUTPUT_PORT_ID,
					() -> BlockEntityType.Builder.of(
							(blockPos, blockState) -> new MutliBlockIOPortBlockEntity(blockPos,
									blockState, false),
							TestModBlocks.MACHINE_OUTPUT_PORT_BLOCK.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperFurnaceBlockEntity>> SUPER_FURNACE_BLOCK_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register("super_furnace", () -> BlockEntityType.Builder
					.of(SuperFurnaceBlockEntity::new, TestModBlocks.SUPER_FURNACE_BLOCK.get())
					.build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrusherBlockEntity>> CRUSHER_BLOCK_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register("crusher", () -> BlockEntityType.Builder
					.of(CrusherBlockEntity::new, TestModBlocks.CRUSHER_BLOCK.get()).build(null));

	public static void register(IEventBus modEventBus) {
		BLOCK_ENTITY_TYPES.register(modEventBus);
	}
}
