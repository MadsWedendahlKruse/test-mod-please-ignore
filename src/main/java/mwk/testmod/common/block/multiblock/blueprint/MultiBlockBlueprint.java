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
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.controller.MultiBlockControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A blueprint for a multiblock structure.
 */
public class MultiBlockBlueprint {

    // The name of the multiblock structure.
    // TODO: Is this necessary?
    private String name;
    // The controller block of the multiblock structure.
    private MultiBlockControllerBlock controller;
    // The positions of the blocks that make up the multiblock structure.
    private BlockPos[] positions;
    // The blockstates of the blocks that make up the multiblock structure.
    private BlockState[] states;
    // Corners of the bounding box of the multiblock structure.
    private BlockPos minCorner;
    private BlockPos maxCorner;

    /**
     * A helper class to hold the data for a multiblock blueprint.
     */
    private static class BlueprintData {
        public String name;
        public MultiBlockControllerBlock controller;
        public BlockPos[] positions;
        public BlockState[] states;
    }

    /**
     * Construct a new multiblock blueprint. See
     * {@link #create(String, MultiBlockControllerBlock, BlockPos[], BlockState[])}.
     */
    private MultiBlockBlueprint(String name, MultiBlockControllerBlock controller,
            BlockPos[] positions, BlockState[] states) {
        if (positions.length != states.length) {
            throw new IllegalArgumentException("Positions and states must be the same length.");
        }
        this.name = name;
        this.controller = controller;
        // TODO: Pretty inefficient way to do this.
        // Initialize the corners to the controller position.
        this.minCorner = new BlockPos(0, 0, 0);
        this.maxCorner = new BlockPos(0, 0, 0);
        // Iterate through the positions and update the corners.
        for (BlockPos pos : positions) {
            // Update the min corner.
            if (pos.getX() < this.minCorner.getX()) {
                this.minCorner = this.minCorner.offset(pos.getX(), 0, 0);
            }
            if (pos.getY() < this.minCorner.getY()) {
                this.minCorner = this.minCorner.offset(0, pos.getY(), 0);
            }
            if (pos.getZ() < this.minCorner.getZ()) {
                this.minCorner = this.minCorner.offset(0, 0, pos.getZ());
            }
            // Update the max corner.
            if (pos.getX() > this.maxCorner.getX()) {
                this.maxCorner = this.maxCorner.offset(pos.getX(), 0, 0);
            }
            if (pos.getY() > this.maxCorner.getY()) {
                this.maxCorner = this.maxCorner.offset(0, pos.getY(), 0);
            }
            if (pos.getZ() > this.maxCorner.getZ()) {
                this.maxCorner = this.maxCorner.offset(0, 0, pos.getZ());
            }
        }
        this.positions = positions;
        this.states = states;
        controller.setBlueprint(this);
    }

    /**
     * Construct a new multiblock blueprint from a blueprint data object.
     * 
     * @param data The blueprint data object to construct the blueprint from.
     */
    private MultiBlockBlueprint(BlueprintData data) {
        this(data.name, data.controller, data.positions, data.states);
    }

    /**
     * Create a new multiblock blueprint.
     * 
     * @param name The name of the multiblock structure.
     * @param controller The controller block of the multiblock structure. The controller block, and
     *        by extension the blueprint, is assumed to be facing north.
     * @param positions The positions of the blocks that make up the multiblock structure. The
     *        positions must be relative to the controller block.
     * @param states The blockstates of the blocks that make up the multiblock structure. The states
     *        must be in the same order as the positions.
     * @return The new multiblock blueprint.
     * @throws IllegalArgumentException If the positions and states are not the same length.
     */
    public static MultiBlockBlueprint create(String name, MultiBlockControllerBlock controller,
            BlockPos[] positions, BlockState[] states) {
        return new MultiBlockBlueprint(name, controller, positions, states);
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
            data.states = new BlockState[blockKeys.size()];
            boolean foundController = false;
            for (int i = 0; i < blockKeys.size(); i++) {
                char symbol = blockKeys.get(i);
                String blockIdentifier = blockIdMap.get(symbol);
                Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(blockIdentifier));
                data.states[i] = block.defaultBlockState();
                // TODO: Maybe this will give a false positive if the block has another
                // boolean property. Alternatively we could check if the block is an
                // instance of MultiBlockPartBlock.
                if (!block.defaultBlockState().hasProperty(MultiBlockPartBlock.IS_FORMED)) {
                    throw new IllegalArgumentException("Block with identifier " + blockIdentifier
                            + " does not have the " + "property \"is_formed\".");
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
            data.positions = new BlockPos[positions.size()];
            positions.toArray(data.positions);
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
     * Get the rotated positions of the blocks that make up the multiblock structure based on the
     * facing of the controller block.
     * 
     * @param direction The direction the controller block is facing.
     * @return The rotated positions of the blocks that make up the multiblock structure.
     */
    public final BlockPos[] getRotatedPositions(Direction direction) {
        BlockPos[] rotatedPositions = new BlockPos[this.positions.length];
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
            case NORTH:
            default:
                rotation = Rotation.NONE;
                break;
        }
        for (int i = 0; i < this.positions.length; i++) {
            rotatedPositions[i] = this.positions[i].rotate(rotation);
        }
        return rotatedPositions;
    }

    /**
     * A helper class to hold the result of a blueprint check. The result contains the indices of
     * the blocks in the blueprint that are empty or have the wrong block.
     */
    public static class BlueprintCheckResult {
        private final ArrayList<Integer> missingBlockIndices;
        private final ArrayList<Integer> wrongBlockIndices;

        public BlueprintCheckResult() {
            this.missingBlockIndices = new ArrayList<Integer>();
            this.wrongBlockIndices = new ArrayList<Integer>();
        }

        public BlueprintCheckResult(ArrayList<Integer> missingBlockIndices,
                ArrayList<Integer> wrongBlockIndices) {
            this.missingBlockIndices = missingBlockIndices;
            this.wrongBlockIndices = wrongBlockIndices;
        }

        public final ArrayList<Integer> getMissingBlockIndices() {
            return missingBlockIndices;
        }

        public final ArrayList<Integer> getWrongBlockIndices() {
            return wrongBlockIndices;
        }

        public boolean isValid() {
            return missingBlockIndices.isEmpty() && wrongBlockIndices.isEmpty();
        }
    }

    /**
     * Check and return the blocks in the blueprint that are empty or have the wrong block.
     * 
     * @param level The level the controller block is in.
     * @param controllerPos The position of the controller block.
     * @param controllerState The state of the controller block.
     * @return The result of the blueprint check. See {@link BlueprintCheckResult}.
     */
    public BlueprintCheckResult check(Level level, BlockPos controllerPos,
            BlockState controllerState) {
        ArrayList<Integer> missingBlockIndices = new ArrayList<Integer>();
        ArrayList<Integer> wrongBlockIndices = new ArrayList<Integer>();
        // Check if the block at the controller position is the correct block.
        if (controllerState.getBlock() != this.controller) {
            // TODO: This will resolve in a valid blueprint check result. Maybe we should
            // do something else...
            return new BlueprintCheckResult();
        }
        // Rotate the positions based on the facing of the controller block.
        BlockPos[] rotatedPositions =
                getRotatedPositions(controllerState.getValue(MultiBlockControllerBlock.FACING));
        // Check if the blocks are in the correct positions.
        for (int i = 0; i < rotatedPositions.length; i++) {
            BlockPos blueprintPos = rotatedPositions[i];
            BlockState blueprintState = this.states[i];
            BlockState blockState = level.getBlockState(controllerPos.offset(blueprintPos));
            // Check if the block at the position is the correct block.
            if (blockState.getBlock() != blueprintState.getBlock()) {
                // Check if the block at the position is empty.
                if (!blockState.isAir()) {
                    wrongBlockIndices.add(i);
                }
                missingBlockIndices.add(i);
            }
        }
        return new BlueprintCheckResult(missingBlockIndices, wrongBlockIndices);
    }

    /**
     * Check if the blueprint for the multiblock structure associated with the controller block at
     * the given position is valid.
     * 
     * @param level The level the controller block is in.
     * @param controllerPos The position of the controller block.
     * @param controllerState The state of the controller block.
     * @return Whether or not the multiblock structure is valid.
     */
    public boolean isValid(Level level, BlockPos controllerPos, BlockState controllerState) {
        return check(level, controllerPos, controllerState).isValid();
    }

    /**
     * @return The name of the multiblock structure.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The blockstates of the blocks that make up the multiblock structure.
     */
    public final BlockState[] getStates() {
        return this.states;
    }

    /**
     * @return An ItemStack of the block at the given index in the blueprint.
     */
    public ItemStack getItemStack(int index) {
        return new ItemStack(this.states[index].getBlock());
    }

    /**
     * Aggregates item stacks corresponding to specific block indices within a blueprint, merging
     * duplicate items into single stacks with adjusted quantities. This method is useful for
     * consolidating individual block items into a compact list of item stacks, where each stack
     * represents a unique item type and its total quantity based on the provided block indices.
     * This can be particularly beneficial for inventory management or for displaying a summary of
     * required materials.
     * 
     * @param indices An array of indices within the blueprint representing specific blocks to be
     *        consolidated.
     * @param level The level the controller block is in. This is used to differentiate between
     *        missing blocks and incorrect blocks. If null the blocks in the blueprint (missing)
     *        will be used, otherwise the blocks in the level (incorrect) will be used.
     * @param controllerPos The position of the controller block.
     * @param facing The direction the controller block is facing.
     * @return A list of {@link ItemStack}s, where each stack represents a unique item type and its
     *         aggregate quantity from the specified block indices. Stacks are merged based on item
     *         type.
     */
    public ArrayList<ItemStack> getCombinedItemStacks(ArrayList<Integer> indices,
            @Nullable Level level, @Nullable BlockPos controllerPos, @Nullable Direction facing) {
        ArrayList<ItemStack> combinedStacks = new ArrayList<>();
        BlockPos[] rotatedPositions = null;
        if (facing != null) {
            rotatedPositions = getRotatedPositions(facing);
        }
        for (int index : indices) {
            ItemStack newItemStack = ItemStack.EMPTY;
            if (level == null || controllerPos == null) {
                newItemStack = getItemStack(index);
            } else if (rotatedPositions != null) {
                BlockState state =
                        level.getBlockState(controllerPos.offset(rotatedPositions[index]));
                newItemStack = new ItemStack(state.getBlock());
            }
            boolean merged = false;
            for (ItemStack existingStack : combinedStacks) {
                if (ItemStack.isSameItem(newItemStack, existingStack)) {
                    existingStack.grow(1);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                combinedStacks.add(newItemStack.copy());
            }
        }
        return combinedStacks;
    }

    /**
     * Aggregates item stacks corresponding to specific block indices within a blueprint, merging
     * duplicate items into single stacks with adjusted quantities. This method is used for
     * displaying a summary of required materials. See
     * {@link #getCombinedItemStacks(ArrayList, Level, BlockPos, Direction)} for more information.
     * 
     * @param indices An array of indices within the blueprint representing specific blocks to be
     *        consolidated.
     * @return A list of {@link ItemStack}s, where each stack represents a unique item type and its
     *         aggregate quantity from the specified block indices. Stacks are merged based on item
     *         type.
     */
    public ArrayList<ItemStack> getCombinedItemStacks(ArrayList<Integer> indices) {
        return getCombinedItemStacks(indices, null, null, null);
    }

}
