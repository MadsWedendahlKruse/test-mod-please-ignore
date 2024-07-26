package mwk.testmod.common.util.inventory.handler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class InputItemHandler extends ItemHandlerWrapper {

    private final InputValidator inputValidator;
    private final boolean playerInput;

    /**
     * Creates a new input item handler.
     * 
     * @param itemHandler the underlying item handler
     * @param startInputSlot the index of the first input slot
     * @param inputSlots the number of input slots
     * @param inputValidator the input validator, which determines whether an item can be inserted.
     *        See {@link InputValidator#isInputValid(int, ItemStack)}
     * @param playerInput whether the input is from a player
     */
    public InputItemHandler(ItemStackHandler itemHandler, int startInputSlot, int inputSlots,
            InputValidator inputValidator, boolean playerInput) {
        super(itemHandler, startInputSlot, inputSlots);
        this.inputValidator = inputValidator;
        this.playerInput = playerInput;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return inputValidator.isInputValid(slot, stack);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (playerInput) {
            return super.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (isSlotValid(slot)) {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @FunctionalInterface
    public interface InputValidator {
        boolean isInputValid(int slot, ItemStack stack);
    }
}
