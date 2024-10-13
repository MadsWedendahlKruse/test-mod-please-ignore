package mwk.testmod.common.util.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;

public class IOUtils {

    /**
     * Push items from the output slots to adjacent inventories.
     *
     * @param level             the level
     * @param outputItemHandler the output item handler
     * @param pos               the position whose neighbors to push to
     * @param stackSize         the maximum stack size to push
     */
    public static void pushItemOutput(Level level, IItemHandler outputItemHandler, BlockPos pos,
            int startSlot, int endSlot, int stackSize) {
        // Check if the output slots are empty
        boolean empty = true;
        for (int i = startSlot; i < endSlot; i++) {
            if (!outputItemHandler.getStackInSlot(i).isEmpty()) {
                empty = false;
                break;
            }
        }
        if (empty) {
            return;
        }
        for (Direction direction : Direction.values()) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            // Don't inject into itself
            if (handler == null || handler == outputItemHandler) {
                continue;
            }
            for (int i = startSlot; i < endSlot; i++) {
                ItemStack extractedStack = outputItemHandler.extractItem(i, stackSize, true);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                for (int j = 0; j < handler.getSlots(); j++) {
                    ItemStack remainder = handler.insertItem(j, extractedStack, false);
                    outputItemHandler.extractItem(i,
                            extractedStack.getCount() - remainder.getCount(), false);
                    if (remainder.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    public static void pushItemOutput(Level level, IItemHandler outputItemHandler, BlockPos pos,
            int stackSize) {
        pushItemOutput(level, outputItemHandler, pos, 0, outputItemHandler.getSlots(), stackSize);
    }

    public static void pullItems(IItemHandler source, IItemHandler destination,
            int destinationStartSlot, int destinationEndSlot, int stackSize) {
        for (int i = 0; i < source.getSlots(); i++) {
            ItemStack extractedStack = source.extractItem(i, stackSize, true);
            if (extractedStack.isEmpty()) {
                continue;
            }
            for (int j = destinationStartSlot; j < destinationEndSlot; j++) {
                if (!destination.isItemValid(j, extractedStack)) {
                    continue;
                }
                ItemStack remainder = destination.insertItem(j, extractedStack, false);
                source.extractItem(i, extractedStack.getCount() - remainder.getCount(), false);
                if (remainder.isEmpty()) {
                    break;
                }
            }
        }
    }

    /**
     * Pull items from adjacent inventories to the input slots.
     *
     * @param level            the level
     * @param inputItemHandler the input item handler
     * @param pos              the position whose neighbors to input from
     * @param stackSize        the maximum stack size to input
     */
    public static void pullItemInput(Level level, IItemHandler inputItemHandler, BlockPos pos,
            int startSlot, int endSlot, int stackSize) {
        for (Direction direction : Direction.values()) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            // Don't input from itself
            if (handler == null || handler == inputItemHandler) {
                continue;
            }
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack extractedStack = handler.extractItem(i, stackSize, true);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                for (int j = startSlot; j < endSlot; j++) {
                    if (!inputItemHandler.isItemValid(j, extractedStack)) {
                        continue;
                    }
                    ItemStack remainder =
                            inputItemHandler.insertItem(j, extractedStack, false);
                    handler.extractItem(i, extractedStack.getCount() - remainder.getCount(), false);
                    if (remainder.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    public static void pullItemInput(Level level, IItemHandler inputItemHandler, BlockPos pos,
            int stackSize) {
        pullItemInput(level, inputItemHandler, pos, 0, inputItemHandler.getSlots(), stackSize);
    }

    /**
     * Push fluids from the output tanks to adjacent tanks.
     *
     * @param level              the level
     * @param outputFluidHandler the output fluid handler
     * @param pos                the position whose neighbors to push to
     * @param stackSize          the maximum stack size to push
     */
    public static void pushFluidOutput(Level level, IFluidHandler outputFluidHandler, BlockPos pos,
            int startTank, int endTank, int stackSize) {
        // Check if the output tanks are empty
        boolean empty = true;
        for (int i = startTank; i < endTank; i++) {
            if (!outputFluidHandler.getFluidInTank(i).isEmpty()) {
                empty = false;
                break;
            }
        }
        if (empty) {
            return;
        }
        for (Direction direction : Direction.values()) {
            IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            // Don't inject into itself
            if (handler == null || handler == outputFluidHandler) {
                continue;
            }
            for (int i = startTank; i < endTank; i++) {
                FluidStack extractedStack =
                        outputFluidHandler.drain(stackSize, FluidAction.SIMULATE);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                int received = handler.fill(extractedStack, FluidAction.EXECUTE);
                outputFluidHandler.drain(received, FluidAction.EXECUTE);
            }
        }
    }

    public static void pushFluidOutput(Level level, IFluidHandler outputFluidHandler, BlockPos pos,
            int stackSize) {
        pushFluidOutput(level, outputFluidHandler, pos, 0, outputFluidHandler.getTanks(),
                stackSize);
    }

    /**
     * Pull fluids from adjacent tanks to the input tanks.
     *
     * @param level             the level
     * @param inputFluidHandler the input fluid handler
     * @param pos               the position whose neighbors to input from
     * @param stackSize         the maximum stack size to input
     */
    public static void pullFluidInput(Level level, IFluidHandler inputFluidHandler, BlockPos pos,
            int startTank, int endTank, int stackSize) {
        for (Direction direction : Direction.values()) {
            IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            // Don't input from itself
            if (handler == null || handler == inputFluidHandler) {
                continue;
            }
            for (int i = startTank; i < endTank; i++) {
                FluidStack extractedStack = handler.drain(stackSize, FluidAction.SIMULATE);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                int received = inputFluidHandler.fill(extractedStack, FluidAction.EXECUTE);
                handler.drain(received, FluidAction.EXECUTE);
            }
        }
    }

    public static void pullFluidInput(Level level, IFluidHandler inputFluidHandler, BlockPos pos,
            int stackSize) {
        pullFluidInput(level, inputFluidHandler, pos, 0, inputFluidHandler.getTanks(), stackSize);
    }
}
