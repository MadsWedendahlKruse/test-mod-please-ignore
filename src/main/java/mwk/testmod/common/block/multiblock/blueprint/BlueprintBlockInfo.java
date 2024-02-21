package mwk.testmod.common.block.multiblock.blueprint;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Represents a block in a blueprint and its expected and actual state.
 */
public class BlueprintBlockInfo {
    private final BlockPos position;
    private final BlockState expectedState;
    private BlockState actualState = null;

    /**
     * Creates a new blueprint block info.
     * 
     * @param position the position of the block relative to the controller
     * @param expectedState the expected state of the block
     */
    public BlueprintBlockInfo(BlockPos position, BlockState expectedState) {
        this.position = position;
        this.expectedState = expectedState;
    }

    /**
     * @param direction the direction the blueprint is facing
     * @return the position of the block relative to the controller
     */
    public BlockPos getRelativePosition(Direction direction) {
        switch (direction) {
            case SOUTH:
                return position.rotate(Rotation.CLOCKWISE_180);
            case EAST:
                return position.rotate(Rotation.CLOCKWISE_90);
            case WEST:
                return position.rotate(Rotation.COUNTERCLOCKWISE_90);
            case NORTH:
            default:
                return position;
        }
    }

    /**
     * @return the position of the block relative to the controller
     */
    public BlockPos getRelativePosition() {
        return getRelativePosition(Direction.NORTH);
    }

    /**
     * @param controllerPosition the position of the controller
     * @param direction the direction the blueprint is facing
     * @return the absolute position of the block
     */
    public BlockPos getAbsolutePosition(BlockPos controllerPosition, Direction direction) {
        return controllerPosition.offset(getRelativePosition(direction));
    }

    public BlockState getExpectedState() {
        return expectedState;
    }

    public BlockState getActualState() {
        return actualState;
    }

    public void setActualState(BlockState actualState) {
        this.actualState = actualState;
    }

    public boolean isBlockEmpty() {
        return actualState != null && actualState.isAir();
    }

    public boolean isBlockMissing() {
        // TODO: Right now we're only comparing the block rather than the actual state
        // Maybe that's fine? This way it doesn't matter if e.g. the rotation is incorrect
        return actualState != null && !actualState.getBlock().equals(expectedState.getBlock());
    }

    public boolean isBlockIncorrect() {
        return isBlockMissing() && !isBlockEmpty();
    }

    public ItemStack getExpectedItemStack() {
        return new ItemStack(expectedState.getBlock().asItem());
    }

    public ItemStack getActualItemStack() {
        if (actualState == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(actualState.getBlock().asItem());
    }

    public static ArrayList<ItemStack> getItemStacks(Collection<BlueprintBlockInfo> blocks,
            boolean actualItemStack) {
        ArrayList<ItemStack> itemStacks = new ArrayList<ItemStack>();
        for (BlueprintBlockInfo block : blocks) {
            boolean merged = false;
            ItemStack itemStack =
                    actualItemStack ? block.getActualItemStack() : block.getExpectedItemStack();
            for (ItemStack stack : itemStacks) {
                if (ItemStack.isSameItem(stack, itemStack)) {
                    stack.grow(1);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                itemStacks.add(itemStack);
            }
        }
        return itemStacks;
    }
}
