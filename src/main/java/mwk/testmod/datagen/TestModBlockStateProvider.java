package mwk.testmod.datagen;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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
		registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_BASIC.get(), "frame/basic",
				TestModBlocks.MACHINE_FRAME_BASIC_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_REINFORCED.get(), "frame/reinforced",
				TestModBlocks.MACHINE_FRAME_REINFORCED_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_ADVANCED.get(), "frame/advanced",
				TestModBlocks.MACHINE_FRAME_ADVANCED_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_INPUT_PORT.get(), "input_port",
				TestModBlocks.MACHINE_INPUT_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_OUTPUT_PORT.get(), "output_port",
				TestModBlocks.MACHINE_OUTPUT_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_ENERGY_PORT.get(), "energy_port",
				TestModBlocks.MACHINE_ENERGY_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.COPPER_COIL.get(), "copper_coil",
				TestModBlocks.COPPER_COIL_ID, CubeModel.CUBE_COLUMN);

		registerMultiBlockController(TestModBlocks.INDUCTION_FURNACE.get(),
				TestModBlocks.INDUCTION_FURNACE_ID);
		registerMultiBlockController(TestModBlocks.SUPER_ASSEMBLER.get(),
				TestModBlocks.SUPER_ASSEMBLER_ID);
		registerMultiBlockController(TestModBlocks.CRUSHER.get(), TestModBlocks.CRUSHER_ID);
	}

	protected void createCubeAll(String modelPath, String texturePath) {
		models().withExistingParent(modelPath, "minecraft:block/cube_all").texture("all",
				texturePath);
	}

	protected void createCubeColumn(String modelPath, String texturePath) {
		models().withExistingParent(modelPath, "minecraft:block/cube_column")
				.texture("end", texturePath + "_end").texture("side", texturePath + "_side");
	}

	protected void registerMultiBlockPart(MultiBlockPartBlock block, String path,
			String registryName, CubeModel modelType) {
		String machineModelPath = "block/machine/" + path;
		ResourceLocation modelLocation = modLoc(machineModelPath);
		// Create the model for the block
		switch (modelType) {
			case CUBE_ALL -> createCubeAll(machineModelPath, "block/machine/" + path);
			case CUBE_COLUMN -> createCubeColumn(machineModelPath, "block/machine/" + path);
		}
		// Create the model for the item
		itemModels().getBuilder(registryName).parent(models().getExistingFile(modelLocation));
		// Create the blockstate for the block
		ModelFile model = models().getExistingFile(modelLocation);
		ModelFile emptyModel = models().getExistingFile(modLoc("block/empty"));
		getVariantBuilder(block).forAllStatesExcept(state -> {
			boolean formed = state.getValue(MultiBlockPartBlock.FORMED);
			return ConfiguredModel.builder().modelFile(formed ? emptyModel : model).build();
		});
	}

	protected void registerMultiBlockController(MultiBlockControllerBlock block, String name) {
		String machineModelPath = "block/machine/" + name;
		// Create the model for the block
		models().withExistingParent(machineModelPath, "testmod:block/machine/controller")
				.texture("screen", "block/machine/controller/screen/" + name);
		// Create the model for the item
		itemModels().getBuilder(name).parent(models().getExistingFile(modLoc(machineModelPath)));
		// Create the blockstate for the block
		ModelFile modelUnformed = models().getExistingFile(modLoc(machineModelPath));
		ModelFile modelFormed = models().getExistingFile(modLoc("block/multiblock/" + name));
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
}
