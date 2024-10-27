package mwk.testmod.common.block.entity.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.common.util.inventory.handler.InputItemHandler;
import mwk.testmod.common.util.inventory.handler.OutputItemHandler;
import mwk.testmod.common.util.inventory.handler.UpgradeItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

/**
 * A module encapsulating an inventory for a machine block entity.
 */
public class InventoryModule implements MachineModule {

    public static final String NBT_TAG_INVENTORY = "inventory";

    private final int inputSlots;
    private final int outputSlots;
    private final int upgradeSlots;
    private final int inventorySize;

    private final ItemStackHandler inventory;
    private final InputItemHandler inputItemHandlerPlayer;
    private final InputItemHandler inputItemHandlerAutomation;
    private final OutputItemHandler outputItemHandler;
    private final UpgradeItemHandler upgradeItemHandler;
    private final Lazy<CombinedInvWrapper> combinedInventory;

    public InventoryModule(MachineBlockEntity machine, int inputSlots, int outputSlots,
            int upgradeSlots, Consumer<Integer> onSlotChanged,
            InputItemHandler.InputValidator canInsert) {
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.upgradeSlots = upgradeSlots;
        this.inventorySize = inputSlots + outputSlots + upgradeSlots;
        this.inventory = new ItemStackHandler(inventorySize) {
            @Override
            protected void onContentsChanged(int slot) {
                machine.setChanged();
                onSlotChanged.accept(slot);
            }

            @Override
            public int getSlotLimit(int slot) {
                // Only the upgrade handler has a slot limit
                if (upgradeItemHandler.isSlotValid(slot)) {
                    return upgradeItemHandler.getSlotLimit(slot);
                }
                return 64;
            }
        };
        this.inputItemHandlerPlayer = new InputItemHandler(inventory, 0, inputSlots,
                canInsert, true);
        this.inputItemHandlerAutomation = new InputItemHandler(inventory, 0, inputSlots,
                canInsert, false);
        this.outputItemHandler = new OutputItemHandler(inventory, inputSlots, outputSlots);
        this.upgradeItemHandler = new UpgradeItemHandler(inventory, inputSlots + outputSlots,
                upgradeSlots, machine);
        this.combinedInventory = Lazy.of(
                () -> new CombinedInvWrapper(inputItemHandlerPlayer, outputItemHandler,
                        upgradeItemHandler));
    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registries) {
        tag.put(NBT_TAG_INVENTORY, inventory.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        if (tag.contains(NBT_TAG_INVENTORY)) {
            inventory.deserializeNBT(registries, tag.getCompound(NBT_TAG_INVENTORY));
        }
    }

    public IItemHandler getItemHandler(Direction direction) {
        return combinedInventory.get();
    }

    public InputItemHandler getInputItemHandler(Direction direction, boolean player) {
        return player ? inputItemHandlerPlayer : inputItemHandlerAutomation;
    }

    public OutputItemHandler getOutputItemHandler(Direction direction) {
        return outputItemHandler;
    }

    public UpgradeItemHandler getUpgradeItemHandler(Direction direction) {
        return upgradeItemHandler;
    }

    public int getInputSlots() {
        return inputSlots;
    }

    public int getOutputSlots() {
        return outputSlots;
    }

    public int getUpgradeSlots() {
        return upgradeSlots;
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public Container getDrops() {
        SimpleContainer inventory = new SimpleContainer(inventorySize);
        for (int i = 0; i < inventorySize; i++) {
            inventory.setItem(i, this.inventory.getStackInSlot(i));
        }
        return inventory;
    }

    public Container getInputs() {
        SimpleContainer inventory = new SimpleContainer(inputSlots);
        for (int i = 0; i < inputSlots; i++) {
            inventory.setItem(i, this.inventory.getStackInSlot(i));
        }
        return inventory;
    }

    public boolean inputsChanged(Container inputs) {
        for (int i = 0; i < inputSlots; i++) {
            if (!inputs.getItem(i).is(this.inventory.getStackInSlot(i).getItem())) {
                return true;
            }
        }
        return false;
    }

    public List<UpgradeItem> getUpgrades() {
        ArrayList<UpgradeItem> upgrades = new ArrayList<>();
        for (int i = upgradeItemHandler.getStartSlot(); i < upgradeItemHandler.getEndSlot(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.getItem() instanceof UpgradeItem upgrade) {
                upgrades.add(upgrade);
            }
        }
        return upgrades;
    }

    public boolean canInsertItemIntoSlot(int slot, Item item, int count) {
        return inventory.getStackInSlot(slot).isEmpty() || (inventory.getStackInSlot(slot).is(item)
                && inventory.getStackInSlot(slot).getCount() + count <= inventory
                .getSlotLimit(slot));
    }

    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inventory.extractItem(slot, amount, simulate);
    }

    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }
}
