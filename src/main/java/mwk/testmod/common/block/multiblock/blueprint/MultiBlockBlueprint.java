package mwk.testmod.common.block.multiblock.blueprint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A blueprint for a multiblock structure.
 */
public final class MultiBlockBlueprint {

    // Codec for a single layer, which is a list of strings
    private static final Codec<List<String>> LAYER_CODEC = Codec.list(Codec.STRING);

    // Codec for the layers section, which is a list of layers
    private static final Codec<List<List<String>>> LAYERS_CODEC = Codec.list(LAYER_CODEC);

    // Codec for the key section, which is a map of character to block identifier strings
    // String is used because Codec.CHARACTER doesn't exist
    private static final Codec<Map<String, String>> KEY_CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.STRING);

    public static final Codec<MultiBlockBlueprint> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    // We don't need the actual data here
                    .group(LAYERS_CODEC.fieldOf("layers").forGetter(data -> null),
                            KEY_CODEC.fieldOf("key").forGetter(data -> null))
                    .apply(instance, MultiBlockBlueprint::new));

    // The key to the blueprint
    private ResourceKey<MultiBlockBlueprint> key;
    // The name of the blueprint
    private String name;
    // The controller block of the multiblock structure.
    private MultiBlockControllerBlock controller;
    // The blocks that make up the multiblock structure.
    private BlueprintBlockInfo[] blocks;
    // Corners of the bounding box of the multiblock structure.
    private final BlockPos minCorner;
    private final BlockPos maxCorner;
    // AABB cache
    private final Map<Direction, AABB> aabbCache;
    // Shape cache
    private final Map<Direction, VoxelShape> shapeCache;

    public MultiBlockBlueprint(List<List<String>> layers, Map<String, String> key) {
        parseData(layers, key);
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
        this.controller.setBlueprint(this);
        // Initialize the AABB cache
        this.aabbCache = new HashMap<>();
        // Initialize the shape cache
        this.shapeCache = new HashMap<>();
    }

    /**
     * Parse the data from the layers and key sections of the blueprint JSON.
     */
    private void parseData(List<List<String>> layers, Map<String, String> key) {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        ArrayList<String> symbols = new ArrayList<String>();
        for (int i = 0; i < layers.size(); i++) {
            List<String> layer = layers.get(i);
            for (int j = 0; j < layer.size(); j++) {
                String row = layer.get(j);
                for (int k = 0; k < row.length(); k++) {
                    char c = row.charAt(k);
                    if (c == ' ') {
                        continue;
                    }
                    positions.add(new BlockPos(k, i, j));
                    symbols.add(c + "");
                }
            }
        }
        BlockState[] states = new BlockState[symbols.size()];
        boolean foundController = false;
        for (int i = 0; i < symbols.size(); i++) {
            String symbol = symbols.get(i);
            String blockIdentifier = key.get(symbol);
            ResourceLocation blockId = new ResourceLocation(blockIdentifier);
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(blockIdentifier));
            states[i] = block.defaultBlockState();
            // TODO: Maybe this will give a false positive if the block has another
            // boolean property. Alternatively we could check if the block is an
            // instance of MultiBlockPartBlock.
            if (!block.defaultBlockState().hasProperty(MultiBlockPartBlock.FORMED)) {
                throw new IllegalArgumentException("Block with identifier " + blockIdentifier
                        + " does not have the " + "property \"formed\".");
            }
            if (block instanceof MultiBlockControllerBlock controller) {
                foundController = true;
                this.controller = controller;
                // Convert the positions to relative positions.
                BlockPos controllerPos = positions.get(i);
                positions.replaceAll(blockPos -> blockPos.subtract(controllerPos));
                this.key = ResourceKey.create(TestModBlueprints.BLUEPRINT_REGISTRY_KEY, blockId);
                this.name = block.getName().getString();
            }
        }
        if (!foundController) {
            throw new IllegalArgumentException("Blueprint JSON must have a controller block.");
        }
        this.blocks = new BlueprintBlockInfo[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            this.blocks[i] = new BlueprintBlockInfo(positions.get(i), states[i]);
        }
    }

    /**
     * Get the absolute positions of the blocks that make up the multiblock structure.
     *
     * @param controllerPos The position of the controller block.
     * @param direction     The direction the controller block is facing.
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
     * @param level            The level the controller block is in.
     * @param controllerPos    The position of the controller block.
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
     * @param level         The level the controller block is in.
     * @param controllerPos The position of the controller block.
     * @return Whether the multiblock structure is complete.
     */
    public boolean isComplete(Level level, BlockPos controllerPos) {
        return getState(level, controllerPos).isComplete();
    }

    /**
     * @return The key of the multiblock structure.
     */
    public ResourceKey<MultiBlockBlueprint> getKey() {
        return this.key;
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
     *                      returned. If not null the absolute position is returned.
     * @param direction     The direction the controller block is facing.
     * @return The AABB of the multiblock structure.
     */
    public AABB getAABB(@Nullable BlockPos controllerPos, Direction direction) {
        AABB aabb = this.aabbCache.get(direction);
        if (aabb == null) {
            Rotation rotation = switch (direction) {
                case SOUTH -> Rotation.CLOCKWISE_180;
                case EAST -> Rotation.CLOCKWISE_90;
                case WEST -> Rotation.COUNTERCLOCKWISE_90;
                default -> Rotation.NONE;
            };
            BlockPos minCorner = this.minCorner.rotate(rotation);
            BlockPos maxCorner = this.maxCorner.rotate(rotation);
            aabb = AABB.encapsulatingFullBlocks(minCorner, maxCorner);
            this.aabbCache.put(direction, aabb);
        }
        if (controllerPos != null) {
            aabb = aabb.move(controllerPos);
        }
        return aabb;
    }

    /**
     * @return The AABB of the multiblock structure. All positions are relative to the controller
     * block and the controller block is assumed to be facing north. For an absolute AABB use
     * {@link #getAABB(BlockPos, Direction)}.
     */
    public AABB getAABB() {
        return getAABB(null, Direction.NORTH);
    }

    /**
     * @param direction The direction the controller block is facing.
     * @return The shape of the multiblock structure.
     */
    public VoxelShape getShape(Direction direction) {
        VoxelShape shape = this.shapeCache.get(direction);
        if (shape == null) {
            for (BlueprintBlockInfo block : this.blocks) {
                BlockPos pos = block.getRelativePosition(direction);
                VoxelShape blockShape = Shapes.block();
                blockShape = blockShape.move(pos.getX(), pos.getY(), pos.getZ());
                if (shape == null) {
                    shape = blockShape;
                } else {
                    shape = Shapes.join(shape, blockShape, BooleanOp.OR);
                }
            }
            this.shapeCache.put(direction, shape);
        }
        return shape;
    }
}
