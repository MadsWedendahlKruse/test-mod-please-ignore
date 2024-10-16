package mwk.testmod.datagen;

import java.util.HashMap;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.conduit.ConduitBlock;
import mwk.testmod.common.block.conduit.ConduitConnectionType;
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
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class TestModBlockStateProvider extends BlockStateProvider {

    public TestModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TestMod.MODID, exFileHelper);
    }

    private enum CubeModel {
        CUBE_ALL, CUBE_COLUMN
    }

    private enum ControllerType implements StringRepresentable {
        MACHINE, GENERATOR, CAPACITRON;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    @Override
    public void registerStatesAndModels() {
        registerOreBlock(TestModBlocks.ILMENITE_ORE);
        registerOreBlock(TestModBlocks.DEEPSLATE_ILMENITE_ORE);

        registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_BASIC, CubeModel.CUBE_ALL);
        registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_REINFORCED, CubeModel.CUBE_ALL);
        registerMultiBlockPart(TestModBlocks.MACHINE_FRAME_ADVANCED, CubeModel.CUBE_ALL);
        registerMultiBlockPart(TestModBlocks.MACHINE_ITEM_INPUT_PORT, CubeModel.CUBE_ALL);
        registerMultiBlockPart(TestModBlocks.MACHINE_ITEM_OUTPUT_PORT, CubeModel.CUBE_ALL);
        registerMultiBlockPart(TestModBlocks.MACHINE_FLUID_INPUT_PORT, CubeModel.CUBE_ALL);
        registerMultiBlockPart(TestModBlocks.MACHINE_FLUID_OUTPUT_PORT, CubeModel.CUBE_ALL);
        registerMultiBlockPart(TestModBlocks.MACHINE_ENERGY_PORT, CubeModel.CUBE_ALL);
        registerMultiBlockPart(TestModBlocks.COPPER_COIL, CubeModel.CUBE_COLUMN);
        registerMultiBlockPart(TestModBlocks.ENERGY_CUBE, CubeModel.CUBE_COLUMN);

        registerMultiBlockController(TestModBlocks.INDUCTION_FURNACE, ControllerType.MACHINE);
        registerMultiBlockController(TestModBlocks.SUPER_ASSEMBLER, ControllerType.MACHINE);
        registerMultiBlockController(TestModBlocks.CRUSHER, ControllerType.MACHINE);
        registerMultiBlockController(TestModBlocks.SEPARATOR, ControllerType.MACHINE);
        registerMultiBlockController(TestModBlocks.STAMPING_PRESS, ControllerType.MACHINE);

        registerMultiBlockController(TestModBlocks.REDSTONE_GENERATOR, ControllerType.GENERATOR,
                true);
        registerMultiBlockController(TestModBlocks.GEOTHERMAL_GENERATOR, ControllerType.GENERATOR,
                true);
        registerMultiBlockController(TestModBlocks.STIRLING_GENERATOR, ControllerType.GENERATOR,
                true);

        // TODO: Completely new ControllerType?
        registerMultiBlockController(TestModBlocks.CAPACITRON, ControllerType.CAPACITRON);

        registerConduitBlock(TestModBlocks.CONDUIT_ITEM.get(), "minecraft:solid");
        registerConduitBlock(TestModBlocks.CONDUIT_FLUID.get(), "minecraft:cutout");
        registerConduitBlock(TestModBlocks.CONDUIT_ENERGY.get(), "minecraft:solid");
    }

    private void createCubeAll(String modelPath) {
        models().withExistingParent(modelPath, "minecraft:block/cube_all").texture("all",
                modelPath);
    }

    private void createCubeColumn(String modelPath) {
        models().withExistingParent(modelPath, "minecraft:block/cube_column")
                .texture("end", modelPath + "_end").texture("side", modelPath + "_side");
    }

    private void registerMultiBlockPart(DeferredBlock<? extends MultiBlockPartBlock> block,
            CubeModel modelType) {
        String name = block.getId().getPath();
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
        getVariantBuilder(block.get()).forAllStatesExcept(state -> {
            boolean formed = state.getValue(MultiBlockPartBlock.FORMED);
            return ConfiguredModel.builder().modelFile(formed ? emptyModel : model).build();
        });
    }

    private void createControllerBlockModel(String name, String modelPath, ControllerType type) {
        String controllerType = type.getSerializedName();
        models().withExistingParent(modelPath, "testmod:block/multiblock_controller")
                .texture("particle", "block/controller_top_" + controllerType)
                .texture("front", "block/controller_front_" + controllerType)
                .texture("side", "block/controller_side_" + controllerType)
                .texture("top", "block/controller_top_" + controllerType)
                .texture("screen", "block/controller_screen_" + name);
        itemModels().getBuilder(name).parent(models().getExistingFile(modLoc(modelPath)));
    }

    private int getRotationY(Direction dir) {
        return switch (dir) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }

    private void createMultiBlockModel(String name) {
        models().getBuilder("multiblock/" + name)
                .texture("particle", modLoc("block/machine_frame_advanced"))
                .texture("base", modLoc("block/" + name)).customLoader(ObjModelBuilder::begin)
                .modelLocation(modLoc("models/multiblock/" + name + ".obj")).flipV(true).end();
    }

    private static final String WORKING_SUFFIX = "_working";
    private static final String OFF_SUFFIX = "_off";

    private void createMultiBlockModelWorking(String name, boolean working) {
        String modelPath = "multiblock/" + name;
        if (working) {
            modelPath += WORKING_SUFFIX;
        }
        String workingTexturePath = "block/" + name;
        if (working) {
            workingTexturePath += WORKING_SUFFIX;
        } else {
            workingTexturePath += OFF_SUFFIX;
        }
        models().getBuilder(modelPath)
                .texture("particle", modLoc("block/machine_frame_advanced"))
                .texture("base", modLoc("block/" + name))
                .texture("working", modLoc(workingTexturePath))
                .customLoader(ObjModelBuilder::begin)
                .modelLocation(modLoc("models/multiblock/" + name + ".obj")).flipV(true).end();
    }

    private void registerMultiBlockController(
            DeferredBlock<? extends MultiBlockControllerBlock> block,
            ControllerType type) {
        registerMultiBlockController(block, type, false);
    }

    private void registerMultiBlockController(
            DeferredBlock<? extends MultiBlockControllerBlock> block,
            ControllerType type, boolean modelForWorking) {
        String name = block.getId().getPath();
        String blockModelPath = "block/" + name;
        String multiBlockModelPath = "multiblock/" + name;
        createControllerBlockModel(name, blockModelPath, type);

        if (modelForWorking) {
            createMultiBlockModelWorking(name, false);
            createMultiBlockModelWorking(name, true);
        } else {
            createMultiBlockModel(name);
        }

        // Create the blockstate for the block
        ModelFile modelUnformed = models().getExistingFile(modLoc(blockModelPath));
        ModelFile modelFormed = models().getExistingFile(modLoc(multiBlockModelPath));
        ModelFile modelFormedWorking = modelForWorking
                ? models().getExistingFile(modLoc("multiblock/" + name + WORKING_SUFFIX))
                : null;
        getVariantBuilder(block.get()).forAllStatesExcept(state -> {
            boolean formed = state.getValue(MultiBlockControllerBlock.FORMED);
            boolean working = state.getValue(MultiBlockControllerBlock.WORKING);
            int yRotation = getRotationY(state.getValue(MultiBlockControllerBlock.FACING));
            ModelFile model =
                    formed ? (working && modelForWorking ? modelFormedWorking : modelFormed)
                            : modelUnformed;
            return ConfiguredModel.builder().modelFile(model).rotationY(yRotation).build();
        });
    }

    private void registerOreBlock(DeferredBlock<? extends Block> block) {
        String name = block.getId().getPath();
        String modelPath = "block/" + name;
        // Create the model for the block
        models().withExistingParent(modelPath, "minecraft:block/cube_all").texture("all",
                modelPath);
        // Create the model for the item
        itemModels().getBuilder(name).parent(models().getExistingFile(modLoc(modelPath)));
        // Create the blockstate for the block
        ModelFile model = models().getExistingFile(modLoc(modelPath));
        getVariantBuilder(block.get()).forAllStatesExcept(state -> {
            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private static final String TEXTURE_PATH_INPUT_OUTPUT = "block/conduit_connector_bidirectional";
    private static final String TEXTURE_PATH_INPUT = "block/conduit_connector_pull";
    private static final String TEXTURE_PATH_OUTPUT = "block/conduit_connector_push";

    private void addConduitParts(MultiPartBlockStateBuilder builder, ModelFile model,
            ConduitConnectionType type) {
        builder.part().modelFile(model).addModel().condition(ConduitBlock.NORTH, type).end();
        builder.part().modelFile(model).rotationY(90).addModel().condition(ConduitBlock.EAST, type)
                .end();
        builder.part().modelFile(model).rotationY(180).addModel()
                .condition(ConduitBlock.SOUTH, type).end();
        builder.part().modelFile(model).rotationY(270).addModel().condition(ConduitBlock.WEST, type)
                .end();
        builder.part().modelFile(model).rotationX(270).addModel().condition(ConduitBlock.UP, type)
                .end();
        builder.part().modelFile(model).rotationX(90).addModel().condition(ConduitBlock.DOWN, type)
                .end();
    }

    private void registerConduitBlock(ConduitBlock conduitBlock, String renderType) {
        String conduitType = conduitBlock.getType().name().toLowerCase();
        // Create the models for the conduit block
        String texturePath = "block/conduit_" + conduitType;
        String modelPathBase = "block/conduit_base_" + conduitType;
        String modelPathSide = "block/conduit_side_" + conduitType;
        HashMap<ConduitConnectionType, String> connectorModelPaths = new HashMap<>();
        for (ConduitConnectionType type : ConduitConnectionType.VALUES) {
            if (!type.hasConnector()) {
                continue;
            }
            connectorModelPaths.put(type,
                    "block/conduit_connector_" + type.getSerializedName() + "_" + conduitType);
        }
        models().withExistingParent(modelPathBase, "testmod:block/conduit_base")
                .texture("0", texturePath).texture("particle", texturePath).ao(false)
                .renderType(renderType);
        models().withExistingParent(modelPathSide, "testmod:block/conduit_side")
                .texture("0", texturePath).texture("particle", texturePath).ao(false)
                .renderType(renderType);
        for (ConduitConnectionType type : connectorModelPaths.keySet()) {
            models().withExistingParent(connectorModelPaths.get(type),
                            "testmod:block/conduit_connector")
                    .texture("0", texturePath)
                    .texture("1", "block/conduit_connector_" + type.getSerializedName())
                    .texture("particle", texturePath).ao(false).renderType(renderType);
        }
        // Create the item model for the conduit
        itemModels().getBuilder("item/conduit_" + conduitType)
                .parent(models().getExistingFile(modLoc("item/conduit"))).texture("0", texturePath)
                .texture("particle", texturePath);
        // Create the blockstate for the conduit
        MultiPartBlockStateBuilder builder = getMultipartBuilder(conduitBlock);
        ModelFile modelBase = models().getExistingFile(modLoc(modelPathBase));
        builder.part().modelFile(modelBase).addModel().end();
        ModelFile modelSide = models().getExistingFile(modLoc(modelPathSide));
        addConduitParts(builder, modelSide, ConduitConnectionType.CONDUIT);
        for (ConduitConnectionType type : connectorModelPaths.keySet()) {
            ModelFile modelConnector = models().getExistingFile(
                    modLoc(connectorModelPaths.get(type)));
            addConduitParts(builder, modelConnector, type);
        }
    }
}
