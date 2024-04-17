package mwk.testmod.common.util.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class OutputItemHandler extends ItemHandlerWrapper {

    /**
     * Creates a new output item handler.
     * 
     * @param itemHandler the underlying item handler
     * @param startOutputSlot the index of the first output slot
     * @param outputSlots the number of output slots
     */
    public OutputItemHandler(ItemStackHandler itemHandler, int startOutputSlot, int outputSlots) {
        super(itemHandler, startOutputSlot, outputSlots);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isSlotValid(slot)) {
            return itemHandler.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return false;
    }
}
