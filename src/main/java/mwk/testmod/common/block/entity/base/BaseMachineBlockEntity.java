package mwk.testmod.common.block.entity.base;

import mwk.testmod.common.util.inventory.InputItemHandler;
import mwk.testmod.common.util.inventory.OutputItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * A block entity that stores energy and also has an inventory.
 */
public class BaseMachineBlockEntity extends EnergyBlockEntity {

    public static final String NBT_TAG_INVENTORY = "inventory";

    protected final int inputSlots;
    protected final int outputSlots;
    protected final int inventorySize;

    protected final ItemStackHandler inventory;
    protected final Lazy<InputItemHandler> inputHandler;
    protected final Lazy<OutputItemHandler> outputHandler;

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, EnergyType energyType, int inputSlots, int outputSlots) {
        super(type, pos, state, new EnergyStorage(maxEnergy), energyType);
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        inventorySize = inputSlots + outputSlots;
        inventory = new ItemStackHandler(inventorySize) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return isInputValid(slot, stack);
            }
        };
        inputHandler = Lazy.of(() -> new InputItemHandler(inventory, 0, inputSlots));
        outputHandler = Lazy.of(() -> new OutputItemHandler(inventory, inputSlots, outputSlots));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBT_TAG_INVENTORY, inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NBT_TAG_INVENTORY)) {
            inventory.deserializeNBT(tag.getCompound(NBT_TAG_INVENTORY));
        }
    }

    /**
     * Checks if the given stack can be inserted into the given slot. This is used to check if the
     * stack can be inserted into the input slots. By default, this method returns true, but it can
     * be overridden to provide custom behavior.
     * 
     * @param slot the slot
     * @param stack the stack
     * @return true if the stack can be inserted, false otherwise
     */
    protected boolean isInputValid(int slot, ItemStack stack) {
        return true;
    }

    public IItemHandler getItemHandler(Direction direction) {
        return inventory;
    }

    public InputItemHandler getInputHandler(Direction direction) {
        return inputHandler.get();
    }

    public OutputItemHandler getOutputHandler(Direction direction) {
        return outputHandler.get();
    }

    public int getInputSlots() {
        return inputSlots;
    }

    public int getOutputSlots() {
        return outputSlots;
    }

    public int getInventorySize() {
        return inventorySize;
    }

    /**
     * Get the inventory of the block entity. This is used to drop the inventory when the block is
     * broken. TODO: Not sure if this is the best way to do this.
     */
    public Container getDrops() {
        SimpleContainer inventory = new SimpleContainer(inventorySize);
        for (int i = 0; i < inventorySize; i++) {
            inventory.setItem(i, this.inventory.getStackInSlot(i));
        }
        return inventory;
    }
}
