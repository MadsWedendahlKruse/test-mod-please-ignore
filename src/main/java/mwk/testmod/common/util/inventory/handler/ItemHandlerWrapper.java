package mwk.testmod.common.util.inventory.handler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class ItemHandlerWrapper implements IItemHandlerModifiable {

    protected final IItemHandlerModifiable itemHandler;
    protected final int startSlot;
    protected final int slots;

    public ItemHandlerWrapper(IItemHandlerModifiable itemHandler, int startSlot, int slots) {
        this.itemHandler = itemHandler;
        this.startSlot = startSlot;
        this.slots = slots;
    }

    @Override
    public int getSlots() {
        return itemHandler.getSlots();
    }

    public int getStartSlot() {
        return startSlot;
    }

    public int getEndSlot() {
        return startSlot + slots;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (!isSlotValid(slot)) {
            return 0;
        }
        return itemHandler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!isSlotValid(slot)) {
            return false;
        }
        return itemHandler.isItemValid(slot, stack);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (!isSlotValid(slot)) {
            return ItemStack.EMPTY;
        }
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!isSlotValid(slot)) {
            return stack;
        }
        return itemHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!isSlotValid(slot)) {
            return ItemStack.EMPTY;
        }
        return itemHandler.extractItem(slot, amount, simulate);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (!isSlotValid(slot)) {
            return;
        }
        itemHandler.setStackInSlot(slot, stack);
    }

    public boolean isSlotValid(int slot) {
        return slot >= startSlot && slot < startSlot + slots;
    }
}
