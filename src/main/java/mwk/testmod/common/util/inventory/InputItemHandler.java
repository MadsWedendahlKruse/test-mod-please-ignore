package mwk.testmod.common.util.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InputItemHandler extends ItemHandlerWrapper {

    /**
     * Creates a new input item handler.
     * 
     * @param itemHandler the underlying item handler
     * @param startInputSlot the index of the first input slot
     * @param inputSlots the number of input slots
     */
    public InputItemHandler(ItemStackHandler itemHandler, int startInputSlot, int inputSlots) {
        super(itemHandler, startInputSlot, inputSlots);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot >= startSlot && slot < startSlot + slots) {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }
}
