package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.cable.CableBlockEntity;
import mwk.testmod.common.block.entity.CrusherBlockEntity;
import mwk.testmod.common.block.entity.InductionFurnaceBlockEntity;
import mwk.testmod.common.block.entity.SeparatorBlockEntity;
import mwk.testmod.common.block.entity.TeleporterBlockEntity;
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

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockPartBlockEntity>> MULTI_BLOCK_PART_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register("multi_block_part",
					() -> BlockEntityType.Builder.of(MultiBlockPartBlockEntity::new,
							TestModBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get)
									.filter(block -> block instanceof MultiBlockPartBlock)
									.toArray(Block[]::new))
							.build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockEnergyPortBlockEntity>> MULTI_ENERGY_PORT_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.MACHINE_ENERGY_PORT_ID,
					() -> BlockEntityType.Builder.of(MultiBlockEnergyPortBlockEntity::new,
							TestModBlocks.MACHINE_ENERGY_PORT.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MutliBlockIOPortBlockEntity>> MULTI_INPUT_PORT_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.MACHINE_INPUT_PORT_ID,
					() -> BlockEntityType.Builder
							.of((blockPos, blockState) -> new MutliBlockIOPortBlockEntity(blockPos,
									blockState, true), TestModBlocks.MACHINE_INPUT_PORT.get())
							.build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MutliBlockIOPortBlockEntity>> MULTI_OUTPUT_PORT_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.MACHINE_OUTPUT_PORT_ID,
					() -> BlockEntityType.Builder
							.of((blockPos, blockState) -> new MutliBlockIOPortBlockEntity(blockPos,
									blockState, false), TestModBlocks.MACHINE_OUTPUT_PORT.get())
							.build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InductionFurnaceBlockEntity>> INDUCTION_FURNACE_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.INDUCTION_FURNACE_ID,
					() -> BlockEntityType.Builder.of(InductionFurnaceBlockEntity::new,
							TestModBlocks.INDUCTION_FURNACE.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrusherBlockEntity>> CRUSHER_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.CRUSHER_ID, () -> BlockEntityType.Builder
					.of(CrusherBlockEntity::new, TestModBlocks.CRUSHER.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeparatorBlockEntity>> SEPARATOR_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.SEPARATOR_ID, () -> BlockEntityType.Builder
					.of(SeparatorBlockEntity::new, TestModBlocks.SEPARATOR.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleporterBlockEntity>> TELEPORTER_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.TELEPORTER_ID, () -> BlockEntityType.Builder
					.of(TeleporterBlockEntity::new, TestModBlocks.TELEPORTER.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> CABLE_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.CABLE_ID, () -> BlockEntityType.Builder
					.of(CableBlockEntity::new, TestModBlocks.CABLE.get()).build(null));

	public static void register(IEventBus modEventBus) {
		BLOCK_ENTITY_TYPES.register(modEventBus);
	}
}
