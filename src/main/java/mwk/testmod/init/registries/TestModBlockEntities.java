package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.EnergyConduitBlockEntity;
import mwk.testmod.common.block.conduit.FluidConduitBlockEntity;
import mwk.testmod.common.block.conduit.ItemConduitBlockEntity;
import mwk.testmod.common.block.entity.CapacitronBlockEntity;
import mwk.testmod.common.block.entity.CrusherBlockEntity;
import mwk.testmod.common.block.entity.GeothermalGeneratorBlockEntity;
import mwk.testmod.common.block.entity.InductionFurnaceBlockEntity;
import mwk.testmod.common.block.entity.RedstoneGeneratorBlockEntity;
import mwk.testmod.common.block.entity.SeparatorBlockEntity;
import mwk.testmod.common.block.entity.StirlingGeneratorBlockEntity;
import mwk.testmod.common.block.entity.TeleporterBlockEntity;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.entity.MultiBlockEnergyPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockFluidIOPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockIOPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockItemIOPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModBlockEntities {

    private TestModBlockEntities() {
    }

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
            registerSimpleBlockEntity(TestModBlocks.MACHINE_ENERGY_PORT,
                    MultiBlockEnergyPortBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockItemIOPortBlockEntity>> MULTI_ITEM_INPUT_PORT_ENTITY_TYPE =
            registerIOPortBlockEntity(TestModBlocks.MACHINE_ITEM_INPUT_PORT,
                    MultiBlockItemIOPortBlockEntity::new, true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockItemIOPortBlockEntity>> MULTI_ITEM_OUTPUT_PORT_ENTITY_TYPE =
            registerIOPortBlockEntity(TestModBlocks.MACHINE_ITEM_OUTPUT_PORT,
                    MultiBlockItemIOPortBlockEntity::new, false);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockFluidIOPortBlockEntity>> MULTI_FLUID_INPUT_PORT_ENTITY_TYPE =
            registerIOPortBlockEntity(TestModBlocks.MACHINE_FLUID_INPUT_PORT,
                    MultiBlockFluidIOPortBlockEntity::new, true);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MultiBlockFluidIOPortBlockEntity>> MULTI_FLUID_OUTPUT_PORT_ENTITY_TYPE =
            registerIOPortBlockEntity(TestModBlocks.MACHINE_FLUID_OUTPUT_PORT,
                    MultiBlockFluidIOPortBlockEntity::new, false);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InductionFurnaceBlockEntity>> INDUCTION_FURNACE_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.INDUCTION_FURNACE,
                    InductionFurnaceBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrusherBlockEntity>> CRUSHER_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.CRUSHER, CrusherBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeparatorBlockEntity>> SEPARATOR_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.SEPARATOR, SeparatorBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleporterBlockEntity>> TELEPORTER_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.TELEPORTER, TeleporterBlockEntity::new);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneGeneratorBlockEntity>> REDSTONE_GENERATOR_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.REDSTONE_GENERATOR,
                    RedstoneGeneratorBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeothermalGeneratorBlockEntity>> GEOTHERMAL_GENERATOR_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.GEOTHERMAL_GENERATOR,
                    GeothermalGeneratorBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StirlingGeneratorBlockEntity>> STIRLING_GENERATOR_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.STIRLING_GENERATOR,
                    StirlingGeneratorBlockEntity::new);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CapacitronBlockEntity>> CAPACITRON_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.CAPACITRON, CapacitronBlockEntity::new);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemConduitBlockEntity>> CONDUIT_ITEM_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.CONDUIT_ITEM, ItemConduitBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyConduitBlockEntity>> CONDUIT_ENERGY_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.CONDUIT_ENERGY, EnergyConduitBlockEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidConduitBlockEntity>> CONDUIT_FLUID_ENTITY_TYPE =
            registerSimpleBlockEntity(TestModBlocks.CONDUIT_FLUID, FluidConduitBlockEntity::new);

    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerSimpleBlockEntity(
            DeferredBlock<? extends Block> block,
            BlockEntityType.BlockEntitySupplier<T> factory) {
        return BLOCK_ENTITY_TYPES.register(block.getId().getPath(),
                () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    @FunctionalInterface
    public interface MultiBlockIOPortBlockEntityFactory<T extends MultiBlockIOPortBlockEntity> {

        T create(BlockPos pos, BlockState state, boolean isInput);
    }

    public static <T extends MultiBlockIOPortBlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerIOPortBlockEntity(
            DeferredBlock<? extends Block> block,
            MultiBlockIOPortBlockEntityFactory<T> factory,
            boolean isInput) {
        return BLOCK_ENTITY_TYPES.register(block.getId().getPath(),
                () -> BlockEntityType.Builder.of(
                        (blockPos, blockState) -> factory.create(blockPos, blockState, isInput),
                        block.get()).build(null));
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}
