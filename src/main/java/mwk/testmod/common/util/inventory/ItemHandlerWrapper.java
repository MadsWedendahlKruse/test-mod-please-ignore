package mwk.testmod.common.util.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemHandlerWrapper implements IItemHandler {

    protected final IItemHandler itemHandler;
    protected final int startSlot;
    protected final int slots;

    public ItemHandlerWrapper(IItemHandler itemHandler, int startSlot, int slots) {
        this.itemHandler = itemHandler;
        this.startSlot = startSlot;
        this.slots = slots;
    }

    @Override
    public int getSlots() {
        return itemHandler.getSlots();
    }

    @Override
    public int getSlotLimit(int slot) {
        return itemHandler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return itemHandler.isItemValid(slot, stack);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return itemHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return itemHandler.extractItem(slot, amount, simulate);
    }
}
