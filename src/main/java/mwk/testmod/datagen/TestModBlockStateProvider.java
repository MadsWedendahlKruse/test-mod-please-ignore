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

	@Override
	protected void registerStatesAndModels() {
		registerMultiBLockPart(TestModBlocks.MACHINE_FRAME_BASIC_BLOCK.get(), "frame/basic",
				TestModBlocks.MACHINE_FRAME_BASIC_ID);
		registerMultiBLockPart(TestModBlocks.MACHINE_FRAME_REINFORCED_BLOCK.get(),
				"frame/reinforced", TestModBlocks.MACHINE_FRAME_REINFORCED_ID);
		registerMultiBLockPart(TestModBlocks.MACHINE_FRAME_ADVANCED_BLOCK.get(), "frame/advanced",
				TestModBlocks.MACHINE_FRAME_ADVANCED_ID);
		registerMultiBLockPart(TestModBlocks.MACHINE_INPUT_PORT_BLOCK.get(), "input_port",
				TestModBlocks.MACHINE_INPUT_PORT_ID);
		registerMultiBLockPart(TestModBlocks.MACHINE_OUTPUT_PORT_BLOCK.get(), "output_port",
				TestModBlocks.MACHINE_OUTPUT_PORT_ID);
		registerMultiBLockPart(TestModBlocks.MACHINE_ENERGY_PORT_BLOCK.get(), "energy_port",
				TestModBlocks.MACHINE_ENERGY_PORT_ID);

		registerMultiBlockController(TestModBlocks.SUPER_FURNACE_BLOCK.get(),
				TestModBlocks.SUPER_FURNACE_ID);
		registerMultiBlockController(TestModBlocks.SUPER_ASSEMBLER_BLOCK.get(),
				TestModBlocks.SUPER_ASSEMBLER_ID);
		registerMultiBlockController(TestModBlocks.CRUSHER_BLOCK.get(), TestModBlocks.CRUSHER_ID);
	}

	protected void registerMultiBLockPart(MultiBlockPartBlock block, String path,
			String registryName) {
		String machineModelPath = "block/machine/" + path;
		ResourceLocation modelLocation = modLoc(machineModelPath);
		// Create the model for the block
		models().withExistingParent(machineModelPath, "minecraft:block/cube_all").texture("all",
				"block/machine/" + path);
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
