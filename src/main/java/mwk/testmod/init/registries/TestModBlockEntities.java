package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.EnergyConduitBlockEntity;
import mwk.testmod.common.block.conduit.FluidConduitBlockEntity;
import mwk.testmod.common.block.conduit.ItemConduitBlockEntity;
import mwk.testmod.common.block.entity.CrusherBlockEntity;
import mwk.testmod.common.block.entity.GeothermalGeneratorBlockEntity;
import mwk.testmod.common.block.entity.InductionFurnaceBlockEntity;
import mwk.testmod.common.block.entity.RedstoneGeneratorBlockEntity;
import mwk.testmod.common.block.entity.SeparatorBlockEntity;
import mwk.testmod.common.block.entity.TeleporterBlockEntity;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.entity.MultiBlockEnergyPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockPartBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockFluidIOPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockItemIOPortBlockEntity;
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

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockItemIOPortBlockEntity>> MULTI_ITEM_INPUT_PORT_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES
					.register(TestModBlocks.MACHINE_ITEM_INPUT_PORT_ID,
							() -> BlockEntityType.Builder.of(
									(blockPos, blockState) -> new MultiBlockItemIOPortBlockEntity(
											blockPos, blockState, true),
									TestModBlocks.MACHINE_ITEM_INPUT_PORT.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockItemIOPortBlockEntity>> MULTI_ITEM_OUTPUT_PORT_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.MACHINE_ITEM_OUTPUT_PORT_ID,
					() -> BlockEntityType.Builder.of(
							(blockPos, blockState) -> new MultiBlockItemIOPortBlockEntity(blockPos,
									blockState, false),
							TestModBlocks.MACHINE_ITEM_OUTPUT_PORT.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockFluidIOPortBlockEntity>> MULTI_FLUID_INPUT_PORT_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES
					.register(TestModBlocks.MACHINE_FLUID_INPUT_PORT_ID,
							() -> BlockEntityType.Builder.of(
									(blockPos, blockState) -> new MultiBlockFluidIOPortBlockEntity(
											blockPos, blockState, true),
									TestModBlocks.MACHINE_FLUID_INPUT_PORT.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockFluidIOPortBlockEntity>> MULTI_FLUID_OUTPUT_PORT_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.MACHINE_FLUID_OUTPUT_PORT_ID,
					() -> BlockEntityType.Builder.of(
							(blockPos, blockState) -> new MultiBlockFluidIOPortBlockEntity(blockPos,
									blockState, false),
							TestModBlocks.MACHINE_FLUID_OUTPUT_PORT.get()).build(null));

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

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneGeneratorBlockEntity>> REDSTONE_GENERATOR_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.REDSTONE_GENERATOR_ID,
					() -> BlockEntityType.Builder.of(RedstoneGeneratorBlockEntity::new,
							TestModBlocks.REDSTONE_GENERATOR.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeothermalGeneratorBlockEntity>> GEOTHERMAL_GENERATOR_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.GEOTHERMAL_GENERATOR_ID,
					() -> BlockEntityType.Builder.of(GeothermalGeneratorBlockEntity::new,
							TestModBlocks.GEOTHERMAL_GENERATOR.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemConduitBlockEntity>> CONDUIT_ITEM_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.CONDUIT_ITEM_ID, () -> BlockEntityType.Builder
					.of(ItemConduitBlockEntity::new, TestModBlocks.CONDUIT_ITEM.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyConduitBlockEntity>> CONDUIT_ENERGY_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.CONDUIT_ENERGY_ID,
					() -> BlockEntityType.Builder
							.of(EnergyConduitBlockEntity::new, TestModBlocks.CONDUIT_ENERGY.get())
							.build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidConduitBlockEntity>> CONDUIT_FLUID_ENTITY_TYPE =
			BLOCK_ENTITY_TYPES.register(TestModBlocks.CONDUIT_FLUID_ID,
					() -> BlockEntityType.Builder
							.of(FluidConduitBlockEntity::new, TestModBlocks.CONDUIT_FLUID.get())
							.build(null));

	public static void register(IEventBus modEventBus) {
		BLOCK_ENTITY_TYPES.register(modEventBus);
	}
}
