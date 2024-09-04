package mwk.testmod.init.registries;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.ConduitBlock;
import mwk.testmod.common.block.conduit.ConduitType;
import mwk.testmod.common.block.entity.CapacitronBlockEntity;
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
import mwk.testmod.common.block.multiblock.MultiBlockIOPortBlock.MultiBlockPortBlockEntityFactory;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.entity.MultiBlockFluidIOPortBlockEntity;
import mwk.testmod.common.block.multiblock.entity.MultiBlockItemIOPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModBlocks {

    private TestModBlocks() {
    }

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(TestMod.MODID);

    // Normal blocks
    public static final DeferredBlock<Block> ILMENITE_ORE = registerBlockWithItem("ilmenite_ore",
            () -> new Block(Blocks.IRON_ORE.properties()));
    public static final DeferredBlock<Block> DEEPSLATE_ILMENITE_ORE = registerBlockWithItem(
            "deepslate_ilmenite_ore", () -> new Block(Blocks.DEEPSLATE_IRON_ORE.properties()));

    // Multiblock parts
    public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_BASIC = registerMultiBlockPart(
            "machine_frame_basic");
    public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_REINFORCED = registerMultiBlockPart(
            "machine_frame_reinforced");
    public static final DeferredBlock<MultiBlockPartBlock> MACHINE_FRAME_ADVANCED = registerMultiBlockPart(
            "machine_frame_advanced");
    public static final DeferredBlock<MultiBlockIOPortBlock> MACHINE_ITEM_INPUT_PORT = registerMultiBlockIOPort(
            "machine_item_input_port", true, MultiBlockItemIOPortBlockEntity::new);
    public static final DeferredBlock<MultiBlockIOPortBlock> MACHINE_ITEM_OUTPUT_PORT = registerMultiBlockIOPort(
            "machine_item_output_port", false, MultiBlockItemIOPortBlockEntity::new);
    public static final DeferredBlock<MultiBlockIOPortBlock> MACHINE_FLUID_INPUT_PORT = registerMultiBlockIOPort(
            "machine_fluid_input_port", true, MultiBlockFluidIOPortBlockEntity::new);
    public static final DeferredBlock<MultiBlockIOPortBlock> MACHINE_FLUID_OUTPUT_PORT = registerMultiBlockIOPort(
            "machine_fluid_output_port", false, MultiBlockFluidIOPortBlockEntity::new);
    public static final DeferredBlock<MultiBlockEnergyPortBlock> MACHINE_ENERGY_PORT = registerBlockWithItem(
            "machine_energy_port", () -> new MultiBlockEnergyPortBlock(getMachineProperties()));
    public static final DeferredBlock<MultiBlockPartBlock> COPPER_COIL = registerMultiBlockPart(
            "copper_coil");
    public static final DeferredBlock<MultiBlockPartBlock> ENERGY_CUBE = registerMultiBlockPart(
            "energy_cube");

    // --- Multiblock controllers ---
    // Machines
    public static final DeferredBlock<MultiBlockControllerBlock> INDUCTION_FURNACE = registerMultiBlockController(
            "induction_furnace", InductionFurnaceBlockEntity::new);
    // TODO: Replace with the actual block entity
    public static final DeferredBlock<MultiBlockControllerBlock> SUPER_ASSEMBLER = registerMultiBlockController(
            "super_assembler", InductionFurnaceBlockEntity::new);
    public static final DeferredBlock<MultiBlockControllerBlock> CRUSHER = registerMultiBlockController(
            "crusher", CrusherBlockEntity::new);
    public static final DeferredBlock<MultiBlockControllerBlock> SEPARATOR = registerMultiBlockController(
            "separator", SeparatorBlockEntity::new);
    public static final DeferredBlock<MultiBlockControllerBlock> TELEPORTER = registerMultiBlockController(
            "teleporter", TeleporterBlockEntity::new);

    // Generators
    public static final DeferredBlock<MultiBlockControllerBlock> REDSTONE_GENERATOR = registerMultiBlockController(
            "redstone_generator", RedstoneGeneratorBlockEntity::new);
    public static final DeferredBlock<MultiBlockControllerBlock> GEOTHERMAL_GENERATOR = registerMultiBlockController(
            "geothermal_generator", GeothermalGeneratorBlockEntity::new);
    public static final DeferredBlock<MultiBlockControllerBlock> STIRLING_GENERATOR = registerMultiBlockController(
            "stirling_generator", StirlingGeneratorBlockEntity::new);

    // Energy storage
    public static final DeferredBlock<MultiBlockControllerBlock> CAPACITRON = registerMultiBlockController(
            "capacitron", CapacitronBlockEntity::new);
    // ---

    public static final DeferredBlock<HologramBlock> HOLOGRAM =
            BLOCKS.register("hologram", () -> new HologramBlock());

    // Conduits
    public static final DeferredBlock<ConduitBlock> CONDUIT_ITEM =
            registerBlockWithItem("conduit_item",
                    () -> new ConduitBlock(BlockBehaviour.Properties.of(), ConduitType.ITEM));
    public static final DeferredBlock<ConduitBlock> CONDUIT_FLUID =
            registerBlockWithItem("conduit_fluid",
                    () -> new ConduitBlock(BlockBehaviour.Properties.of(), ConduitType.FLUID));
    public static final DeferredBlock<ConduitBlock> CONDUIT_ENERGY =
            registerBlockWithItem("conduit_energy",
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

    public static DeferredBlock<MultiBlockPartBlock> registerMultiBlockPart(
            String id) {
        return registerBlockWithItem(id, () -> new MultiBlockPartBlock(getMachineProperties()));
    }

    public static DeferredBlock<MultiBlockControllerBlock> registerMultiBlockController(
            String id, BiFunction<BlockPos, BlockState, BlockEntity> blockEntitySupplier) {
        return registerBlockWithItem(id,
                () -> new MultiBlockControllerBlock(getMachineProperties(), blockEntitySupplier));
    }

    public static DeferredBlock<MultiBlockIOPortBlock> registerMultiBlockIOPort(
            String id, boolean isInput,
            MultiBlockPortBlockEntityFactory blockEntitySupplier) {
        return registerBlockWithItem(id,
                () -> new MultiBlockIOPortBlock(getMachineProperties(), isInput,
                        blockEntitySupplier));
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
