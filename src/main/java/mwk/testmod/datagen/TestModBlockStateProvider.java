package mwk.testmod.datagen;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModBlockStateProvider extends BlockStateProvider {

	public TestModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, TestMod.MODID, exFileHelper);
	}

	private enum CubeModel {
		CUBE_ALL, CUBE_COLUMN
	}

	@Override
	protected void registerStatesAndModels() {
		registerOreBlock(TestModBlocks.ILMENITE_ORE.get(), TestModBlocks.ILMENITE_ORE_ID);
		registerOreBlock(TestModBlocks.DEEPSLATE_ILMENITE_ORE.get(),
				TestModBlocks.DEEPSLATE_ILMENITE_ORE_ID);

		registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_BASIC.get(),
				TestModBlocks.MACHINE_FRAME_BASIC_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_REINFORCED.get(),
				TestModBlocks.MACHINE_FRAME_REINFORCED_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_ADVANCED.get(),
				TestModBlocks.MACHINE_FRAME_ADVANCED_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_INPUT_PORT.get(),
				TestModBlocks.MACHINE_INPUT_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_OUTPUT_PORT.get(),
				TestModBlocks.MACHINE_OUTPUT_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_ENERGY_PORT.get(),
				TestModBlocks.MACHINE_ENERGY_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.COPPER_COIL.get(), TestModBlocks.COPPER_COIL_ID,
				CubeModel.CUBE_COLUMN);

		registerMultiBlockController(TestModBlocks.INDUCTION_FURNACE.get(),
				TestModBlocks.INDUCTION_FURNACE_ID);
		registerMultiBlockController(TestModBlocks.SUPER_ASSEMBLER.get(),
				TestModBlocks.SUPER_ASSEMBLER_ID);
		registerMultiBlockController(TestModBlocks.CRUSHER.get(), TestModBlocks.CRUSHER_ID);
		registerMultiBlockController(TestModBlocks.SEPARATOR.get(), TestModBlocks.SEPARATOR_ID);
	}

	protected void createCubeAll(String modelPath) {
		models().withExistingParent(modelPath, "minecraft:block/cube_all").texture("all",
				modelPath);
	}

	protected void createCubeColumn(String modelPath) {
		models().withExistingParent(modelPath, "minecraft:block/cube_column")
				.texture("end", modelPath + "_end").texture("side", modelPath + "_side");
	}

	protected void registerMultiBlockPart(MultiBlockPartBlock block, String name,
			CubeModel modelType) {
		String modelPath = "block/" + name;
		ResourceLocation modelLocation = modLoc(modelPath);
		// Create the model for the block
		switch (modelType) {
			case CUBE_ALL -> createCubeAll(modelPath);
			case CUBE_COLUMN -> createCubeColumn(modelPath);
		}
		// Create the model for the item
		itemModels().getBuilder(name).parent(models().getExistingFile(modelLocation));
		// Create the blockstate for the block
		ModelFile model = models().getExistingFile(modelLocation);
		ModelFile emptyModel = models().getExistingFile(modLoc("block/empty"));
		getVariantBuilder(block).forAllStatesExcept(state -> {
			boolean formed = state.getValue(MultiBlockPartBlock.FORMED);
			return ConfiguredModel.builder().modelFile(formed ? emptyModel : model).build();
		});
	}

	protected void registerMultiBlockController(MultiBlockControllerBlock block, String name) {
		String modelPath = "block/" + name;
		// Create the model for the block
		models().withExistingParent(modelPath, "testmod:block/multiblock_controller")
				.texture("screen", "block/controller_screen_" + name);
		// Create the model for the item
		itemModels().getBuilder(name).parent(models().getExistingFile(modLoc(modelPath)));
		// Create the blockstate for the block
		ModelFile modelUnformed = models().getExistingFile(modLoc(modelPath));
		ModelFile modelFormed = models().getExistingFile(modLoc("multiblock/" + name));
		getVariantBuilder(block).forAllStatesExcept(state -> {
			Direction dir = state.getValue(MultiBlockControllerBlock.FACING);
			boolean formed = state.getValue(MultiBlockControllerBlock.FORMED);
			int yRotation = switch (dir) {
				case EAST -> 90;
				case SOUTH -> 180;
				case WEST -> 270;
				default -> 0;
			};
			ModelFile model = formed ? modelFormed : modelUnformed;
			return ConfiguredModel.builder().modelFile(model).rotationY(yRotation).build();
		});
	}

	protected void registerOreBlock(Block block, String name) {
		String modelPath = "block/" + name;
		// Create the model for the block
		models().withExistingParent(modelPath, "minecraft:block/cube_all").texture("all",
				modelPath);
		// Create the model for the item
		itemModels().getBuilder(name).parent(models().getExistingFile(modLoc(modelPath)));
		// Create the blockstate for the block
		ModelFile model = models().getExistingFile(modLoc(modelPath));
		getVariantBuilder(block).forAllStatesExcept(state -> {
			return ConfiguredModel.builder().modelFile(model).build();
		});
	}
}
