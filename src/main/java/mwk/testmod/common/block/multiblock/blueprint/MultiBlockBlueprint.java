package mwk.testmod.common.block.multiblock.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * A blueprint for a multiblock structure.
 */
public class MultiBlockBlueprint {

    // The name of the multiblock structure.
    private String name;
    // The controller block of the multiblock structure.
    private MultiBlockControllerBlock controller;
    // The blocks that make up the multiblock structure.
    private BlueprintBlockInfo[] blocks;
    // Corners of the bounding box of the multiblock structure.
    private BlockPos minCorner;
    private BlockPos maxCorner;

    /**
     * A helper class to hold the data for a multiblock blueprint.
     */
    private static class BlueprintData {
        public String name;
        public MultiBlockControllerBlock controller;
        public BlueprintBlockInfo[] blocks;
    }

    /**
     * Construct a new multiblock blueprint. See
     * {@link #create(String, MultiBlockControllerBlock, BlockPos[], BlockState[])}.
     */
    private MultiBlockBlueprint(String name, MultiBlockControllerBlock controller,
            BlueprintBlockInfo[] blocks) {
        this.name = name;
        // Initialize the corners to extreme values to ensure they get updated.
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        // Iterate through the block positions to find the min and max values.
        for (BlueprintBlockInfo blockInfo : blocks) {
            BlockPos pos = blockInfo.getRelativePosition();
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        // Set the min and max corners based on the calculated values.
        this.minCorner = new BlockPos(minX, minY, minZ);
        this.maxCorner = new BlockPos(maxX, maxY, maxZ);
        this.blocks = blocks;
        this.controller = controller;
        controller.setBlueprint(this);
    }

    /**
     * Construct a new multiblock blueprint from a blueprint data object.
     * 
     * @param data The blueprint data object to construct the blueprint from.
     */
    private MultiBlockBlueprint(BlueprintData data) {
        this(data.name, data.controller, data.blocks);
    }

    /**
     * Create a new multiblock blueprint.
     * 
     * @param name The name of the multiblock structure.
     * @param controller The controller block of the multiblock structure. The controller block, and
     *        by extension the blueprint, is assumed to be facing north.
     * @param blocks The blocks that make up the multiblock structure. The positions are relative to
     *        the controller block.
     * @return The new multiblock blueprint.
     */
    public static MultiBlockBlueprint create(String name, MultiBlockControllerBlock controller,
            BlueprintBlockInfo[] blocks) {
        return new MultiBlockBlueprint(name, controller, blocks);
    }

    /**
     * Parse a JSON file to create a new multiblock blueprint.
     * 
     * @param resourceManager The resource manager to load the blueprint from.
     * @param location The resource location of the JSON file.
     * @return The blueprint data object.
     * @throws IllegalArgumentException If the JSON file is not formatted correctly.
     */
    private static BlueprintData parseJson(ResourceManager resourceManager,
            ResourceLocation location) {
        // Initialize the blueprint data object.
        BlueprintData data = new BlueprintData();
        // Extract the name from the location. The name is after the last slash, and
        // before the last period.
        String name = location.getPath();
        name = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));
        name = "multiblock." + TestMod.MODID + "." + name;
        data.name = name;
        try (InputStream stream = resourceManager.open(location)) {
            JsonElement jsonElement =
                    JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            if (!jsonElement.isJsonObject()) {
                throw new IllegalArgumentException("Blueprint JSON must be an object.");
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // Parse the layers of the blueprint.
            JsonArray layers = jsonObject.getAsJsonArray("layers");
            if (layers == null) {
                throw new IllegalArgumentException(
                        "Blueprint JSON must have an element named \"layers\" which "
                                + "must be an array.");
            }
            // Assume each layer has the same dimensions.
            // TODO: What if the layers don't have the same dimensions?
            ArrayList<Character> blockKeys = new ArrayList<Character>();
            ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
            for (int i = 0; i < layers.size(); i++) {
                JsonArray layer = layers.get(i).getAsJsonArray();
                if (layer == null) {
                    throw new IllegalArgumentException(
                            "Each layer in \"layers\" must be an array.");
                }
                for (int j = 0; j < layer.size(); j++) {
                    String row = layer.get(j).getAsString();
                    if (row == null) {
                        throw new IllegalArgumentException(
                                "Each row in the layer must be a string.");
                    }
                    for (int k = 0; k < row.length(); k++) {
                        char c = row.charAt(k);
                        if (c == ' ') {
                            continue;
                        }
                        blockKeys.add(c);
                        positions.add(new BlockPos(k, i, j));
                    }
                }
            }
            // Parse key mappings.
            JsonObject keyMapping = jsonObject.getAsJsonObject("key");
            if (keyMapping == null) {
                throw new IllegalArgumentException(
                        "Blueprint JSON must have an element named \"key\" which "
                                + "must be an object.");
            }
            HashMap<Character, String> blockIdMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : keyMapping.entrySet()) {
                char symbol = entry.getKey().charAt(0);
                String blockIdentifier = entry.getValue().getAsString();
                if (blockIdentifier == null) {
                    throw new IllegalArgumentException("Value of each key must be a string.");
                }
                blockIdMap.put(symbol, blockIdentifier);
            }
            // Convert the block keys to block states.
            BlockState[] states = new BlockState[blockKeys.size()];
            boolean foundController = false;
            for (int i = 0; i < blockKeys.size(); i++) {
                char symbol = blockKeys.get(i);
                String blockIdentifier = blockIdMap.get(symbol);
                Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(blockIdentifier));
                states[i] = block.defaultBlockState();
                // TODO: Maybe this will give a false positive if the block has another
                // boolean property. Alternatively we could check if the block is an
                // instance of MultiBlockPartBlock.
                if (!block.defaultBlockState().hasProperty(MultiBlockPartBlock.FORMED)) {
                    throw new IllegalArgumentException("Block with identifier " + blockIdentifier
                            + " does not have the " + "property \"formed\".");
                }
                // Check if the block is the controller block.
                if (block instanceof MultiBlockControllerBlock) {
                    foundController = true;
                    data.controller = (MultiBlockControllerBlock) block;
                    // Convert the positions to relative positions.
                    BlockPos controllerPos = positions.get(i);
                    for (int j = 0; j < positions.size(); j++) {
                        positions.set(j, positions.get(j).subtract(controllerPos));
                    }
                }
            }
            if (!foundController) {
                throw new IllegalArgumentException("Blueprint JSON must have a controller block.");
            }
            data.blocks = new BlueprintBlockInfo[positions.size()];
            for (int i = 0; i < positions.size(); i++) {
                data.blocks[i] = new BlueprintBlockInfo(positions.get(i), states[i]);
            }
        } catch (IOException e) {
            TestMod.LOGGER.info("Failed to read file: " + location + ", " + e.getMessage());
        }
        return data;
    }

    /**
     * Create a new multiblock blueprint by loading it from a JSON file.
     * 
     * @param resourceManager The resource manager to load the blueprint from.
     * @param location The resource location of the JSON file.
     * @return The new multiblock blueprint.
     */
    public static MultiBlockBlueprint create(ResourceManager resourceManager,
            ResourceLocation location) {
        try {
            BlueprintData data = parseJson(resourceManager, location);
            return new MultiBlockBlueprint(data);
        } catch (Exception e) {
            TestMod.LOGGER.info("Failed to load blueprint: " + location + ", " + e.getMessage());
        }
        return null;
    }

    /**
     * Get the absolute positions of the blocks that make up the multiblock structure.
     * 
     * @param controllerPos The position of the controller block.
     * @param direction The direction the controller block is facing.
     * @return The absolute positions of the blocks.
     */
    public BlockPos[] getAbsolutePositions(BlockPos controllerPos, Direction direction) {
        BlockPos[] positions = new BlockPos[this.blocks.length];
        for (int i = 0; i < this.blocks.length; i++) {
            positions[i] = this.blocks[i].getAbsolutePosition(controllerPos, direction);
        }
        return positions;
    }

    /**
     * Get the state of the multiblock structure associated with the controller block at the given
     * position. This creates a new blueprint state object. If you want to update an existing
     * blueprint state object, use {@link BlueprintState#update()}.
     * 
     * @param level The level the controller block is in.
     * @param controllerPos The position of the controller block.
     * @param controllerFacing The direction the controller block is facing.
     * @return The state of the multiblock structure.
     */
    public BlueprintState getState(Level level, BlockPos controllerPos,
            Direction controllerFacing) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null.");
        }
        if (controllerPos == null) {
            throw new IllegalArgumentException("Controller position cannot be null.");
        }
        if (controllerFacing == null) {
            throw new IllegalArgumentException("Controller facing cannot be null.");
        }
        return new BlueprintState(this, level, controllerPos, controllerFacing);
    }

    /**
     * Get the state of the multiblock structure associated with the controller block at the given
     * position. See {@link #getState(Level, BlockPos, Direction)}.
     */
    public BlueprintState getState(Level level, BlockPos controllerPos) {
        BlockState state = level.getBlockState(controllerPos);
        if (!(state.getBlock() instanceof MultiBlockControllerBlock)) {
            throw new IllegalArgumentException("Block at controller position is not a controller.");
        }
        Direction direction = state.getValue(MultiBlockControllerBlock.FACING);
        return getState(level, controllerPos, direction);
    }

    /**
     * Check if the multiblock structure associated with the controller block at the given position
     * is complete.
     * 
     * @param level The level the controller block is in.
     * @param controllerPos The position of the controller block.
     * @return Whether the multiblock structure is complete.
     */
    public boolean isComplete(Level level, BlockPos controllerPos) {
        return getState(level, controllerPos).isComplete();
    }

    /**
     * @return The name of the multiblock structure.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The blocks that make up the multiblock structure.
     */
    public BlueprintBlockInfo[] getBlocks() {
        return this.blocks;
    }

    /**
     * @return The controller block of the multiblock structure.
     */
    public MultiBlockControllerBlock getController() {
        return this.controller;
    }

    /**
     * @param controllerPos The position of the controller block. If null the relative position is
     *        returned. If not null the absolute position is returned.
     * @param direction The direction the controller block is facing.
     * @return The AABB of the multiblock structure.
     */
    public AABB getAABB(@Nullable BlockPos controllerPos, Direction direction) {
        Rotation rotation;
        switch (direction) {
            case SOUTH:
                rotation = Rotation.CLOCKWISE_180;
                break;
            case EAST:
                rotation = Rotation.CLOCKWISE_90;
                break;
            case WEST:
                rotation = Rotation.COUNTERCLOCKWISE_90;
                break;
            default:
                rotation = Rotation.NONE;
                break;
        }
        BlockPos minCorner = this.minCorner.rotate(rotation);
        BlockPos maxCorner = this.maxCorner.rotate(rotation);
        AABB aabb = AABB.encapsulatingFullBlocks(minCorner, maxCorner);
        if (controllerPos != null) {
            aabb = aabb.move(controllerPos);
        }
        return aabb;
    }

    /**
     * @return The AABB of the multiblock structure. All positions are relative to the controller
     *         block and the controller block is assumed to be facing north. For an absolute AABB
     *         use {@link #getAABB(BlockPos, Direction)}.
     */
    public AABB getAABB() {
        return getAABB(null, Direction.NORTH);
    }
}
