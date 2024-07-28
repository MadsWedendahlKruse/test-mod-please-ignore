package mwk.testmod.datagen;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.ConduitBlock;
import mwk.testmod.common.block.conduit.ConnectorType;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModBlockStateProvider extends BlockStateProvider {

	public TestModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, TestMod.MODID, exFileHelper);
	}

	private enum CubeModel {
		CUBE_ALL, CUBE_COLUMN
	}

	private enum ControllerType implements StringRepresentable {
		MACHINE, GENERATOR;

		@Override
		public String getSerializedName() {
			return name().toLowerCase();
		}
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
		registerMultiBlockPart(TestModBlocks.MACHINE_ITEM_INPUT_PORT.get(),
				TestModBlocks.MACHINE_ITEM_INPUT_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_ITEM_OUTPUT_PORT.get(),
				TestModBlocks.MACHINE_ITEM_OUTPUT_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_FLUID_INPUT_PORT.get(),
				TestModBlocks.MACHINE_FLUID_INPUT_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_FLUID_OUTPUT_PORT.get(),
				TestModBlocks.MACHINE_FLUID_OUTPUT_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.MACHINE_ENERGY_PORT.get(),
				TestModBlocks.MACHINE_ENERGY_PORT_ID, CubeModel.CUBE_ALL);
		registerMultiBlockPart(TestModBlocks.COPPER_COIL.get(), TestModBlocks.COPPER_COIL_ID,
				CubeModel.CUBE_COLUMN);

		registerMultiBlockController(TestModBlocks.INDUCTION_FURNACE.get(),
				TestModBlocks.INDUCTION_FURNACE_ID, ControllerType.MACHINE);
		registerMultiBlockController(TestModBlocks.SUPER_ASSEMBLER.get(),
				TestModBlocks.SUPER_ASSEMBLER_ID, ControllerType.MACHINE);
		registerMultiBlockController(TestModBlocks.CRUSHER.get(), TestModBlocks.CRUSHER_ID,
				ControllerType.MACHINE);
		registerMultiBlockController(TestModBlocks.SEPARATOR.get(), TestModBlocks.SEPARATOR_ID,
				ControllerType.MACHINE);

		registerMultiBlockController(TestModBlocks.REDSTONE_GENERATOR.get(),
				TestModBlocks.REDSTONE_GENERATOR_ID, ControllerType.GENERATOR, true);
		registerMultiBlockController(TestModBlocks.GEOTHERMAL_GENERATOR.get(),
				TestModBlocks.GEOTHERMAL_GENERATOR_ID, ControllerType.GENERATOR, true);

		registerConduitBlock(TestModBlocks.CONDUIT_ITEM.get(), "minecraft:solid");
		registerConduitBlock(TestModBlocks.CONDUIT_FLUID.get(), "minecraft:cutout");
		registerConduitBlock(TestModBlocks.CONDUIT_ENERGY.get(), "minecraft:solid");
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

	protected void createControllerBlockModel(String name, String modelPath, ControllerType type) {
		String controllerType = type.getSerializedName();
		models().withExistingParent(modelPath, "testmod:block/multiblock_controller")
				.texture("particle", "block/controller_top_" + controllerType)
				.texture("front", "block/controller_front_" + controllerType)
				.texture("side", "block/controller_side_" + controllerType)
				.texture("top", "block/controller_top_" + controllerType)
				.texture("screen", "block/controller_screen_" + name);
		itemModels().getBuilder(name).parent(models().getExistingFile(modLoc(modelPath)));
	}

	protected int getRotationY(Direction dir) {
		return switch (dir) {
			case EAST -> 90;
			case SOUTH -> 180;
			case WEST -> 270;
			default -> 0;
		};
	}

	protected void registerMultiBlockController(MultiBlockControllerBlock block, String name,
			ControllerType type) {
		registerMultiBlockController(block, name, type, false);
	}

	protected void registerMultiBlockController(MultiBlockControllerBlock block, String name,
			ControllerType type, boolean modelForWorking) {
		String modelPath = "block/" + name;
		createControllerBlockModel(name, modelPath, type);
		// Create the blockstate for the block
		ModelFile modelUnformed = models().getExistingFile(modLoc(modelPath));
		ModelFile modelFormed = models().getExistingFile(modLoc("multiblock/" + name));
		ModelFile modelFormedWorking = modelForWorking
				? models().getExistingFile(modLoc("multiblock/" + name + "_working"))
				: null;
		getVariantBuilder(block).forAllStatesExcept(state -> {
			boolean formed = state.getValue(MultiBlockControllerBlock.FORMED);
			boolean working = state.getValue(MultiBlockControllerBlock.WORKING);
			int yRotation = getRotationY(state.getValue(MultiBlockControllerBlock.FACING));
			ModelFile model =
					formed ? (working && modelForWorking ? modelFormedWorking : modelFormed)
							: modelUnformed;
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

	protected void registerConduitBlock(ConduitBlock conduitBlock, String renderType) {
		String type = conduitBlock.getType().name().toLowerCase();
		// Create the models for the conduit block
		String texturePath = "block/conduit_" + type;
		String modelPathBase = "block/conduit_base_" + type;
		String modelPathSide = "block/conduit_side_" + type;
		String modelPathSideBlock = "block/conduit_side_block_" + type;
		models().withExistingParent(modelPathBase, "testmod:block/conduit_base")
				.texture("0", texturePath).texture("particle", texturePath).ao(false)
				.renderType(renderType);
		models().withExistingParent(modelPathSide, "testmod:block/conduit_side")
				.texture("0", texturePath).texture("particle", texturePath).ao(false)
				.renderType(renderType);
		models().withExistingParent(modelPathSideBlock, "testmod:block/conduit_side_block")
				.texture("0", texturePath).texture("particle", texturePath).ao(false)
				.renderType(renderType);
		// Create the item model for the conduit
		itemModels().getBuilder("item/conduit_" + type)
				.parent(models().getExistingFile(modLoc("item/conduit"))).texture("0", texturePath)
				.texture("particle", texturePath);
		// Create the blockstate for the conduit
		ModelFile modelBase = models().getExistingFile(modLoc(modelPathBase));
		ModelFile modelSide = models().getExistingFile(modLoc(modelPathSide));
		ModelFile modelSideBlock = models().getExistingFile(modLoc(modelPathSideBlock));
		MultiPartBlockStateBuilder builder = getMultipartBuilder(conduitBlock);
		builder.part().modelFile(modelBase).addModel().end();
		builder.part().modelFile(modelSide).addModel()
				.condition(ConduitBlock.NORTH, ConnectorType.CONDUIT).end();
		builder.part().modelFile(modelSide).rotationY(90).addModel()
				.condition(ConduitBlock.EAST, ConnectorType.CONDUIT).end();
		builder.part().modelFile(modelSide).rotationY(180).addModel()
				.condition(ConduitBlock.SOUTH, ConnectorType.CONDUIT).end();
		builder.part().modelFile(modelSide).rotationY(270).addModel()
				.condition(ConduitBlock.WEST, ConnectorType.CONDUIT).end();
		builder.part().modelFile(modelSide).rotationX(270).addModel()
				.condition(ConduitBlock.UP, ConnectorType.CONDUIT).end();
		builder.part().modelFile(modelSide).rotationX(90).addModel()
				.condition(ConduitBlock.DOWN, ConnectorType.CONDUIT).end();
		builder.part().modelFile(modelSideBlock).addModel()
				.condition(ConduitBlock.NORTH, ConnectorType.BLOCK).end();
		builder.part().modelFile(modelSideBlock).rotationY(90).addModel()
				.condition(ConduitBlock.EAST, ConnectorType.BLOCK).end();
		builder.part().modelFile(modelSideBlock).rotationY(180).addModel()
				.condition(ConduitBlock.SOUTH, ConnectorType.BLOCK).end();
		builder.part().modelFile(modelSideBlock).rotationY(270).addModel()
				.condition(ConduitBlock.WEST, ConnectorType.BLOCK).end();
		builder.part().modelFile(modelSideBlock).rotationX(270).addModel()
				.condition(ConduitBlock.UP, ConnectorType.BLOCK).end();
		builder.part().modelFile(modelSideBlock).rotationX(90).addModel()
				.condition(ConduitBlock.DOWN, ConnectorType.BLOCK).end();
	}
}
