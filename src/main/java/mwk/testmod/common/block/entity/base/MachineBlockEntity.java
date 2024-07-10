package mwk.testmod.common.block.entity.base;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.interfaces.IDescripable;
import mwk.testmod.common.block.interfaces.IUpgradable;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.common.util.inventory.InputItemHandler;
import mwk.testmod.common.util.inventory.OutputItemHandler;
import mwk.testmod.common.util.inventory.UpgradeItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

/**
 * A block entity that stores energy and also has an inventory.
 */
public abstract class MachineBlockEntity extends EnergyBlockEntity
        implements MenuProvider, IUpgradable, IDescripable {

    public static final String NBT_TAG_INVENTORY = "inventory";
    public static final String NBT_TAG_AUTO_INSERT = "autoInsert";
    public static final String NBT_TAG_AUTO_EJECT = "autoEject";

    public static final int IO_SPEED = TestModConfig.MACHINE_IO_SPEED_DEFAULT.get(); // [items/tick]

    protected final int inputSlots;
    protected final int outputSlots;
    protected final int upgradeSlots;
    protected final int inventorySize;

    protected final ItemStackHandler inventory;
    protected final InputItemHandler inputHandlerPlayer;
    protected final InputItemHandler inputHandlerAutomation;
    protected final OutputItemHandler outputHandler;
    protected final UpgradeItemHandler upgradeHandler;
    protected final Lazy<CombinedInvWrapper> combinedInventory;

    private boolean autoInsert;
    private boolean autoEject;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, EnergyType energyType, int inputSlots, int outputSlots,
            int upgradeSlots) {
        super(type, pos, state, new EnergyStorage(maxEnergy), energyType);
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.upgradeSlots = upgradeSlots;
        inventorySize = inputSlots + outputSlots + upgradeSlots;
        inventory = new ItemStackHandler(inventorySize) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public int getSlotLimit(int slot) {
                if (inputHandlerPlayer.isSlotValid(slot)) {
                    return inputHandlerPlayer.getSlotLimit(slot);
                }
                if (outputHandler.isSlotValid(slot)) {
                    return outputHandler.getSlotLimit(slot);
                }
                if (upgradeHandler.isSlotValid(slot)) {
                    return upgradeHandler.getSlotLimit(slot);
                }
                return super.getSlotLimit(slot);
                // TODO: For some reason this doesn't work
                // return combinedInventory.get().getSlotLimit(slot);
            }
        };
        inputHandlerPlayer =
                new InputItemHandler(inventory, 0, inputSlots, this::isInputValid, true);
        inputHandlerAutomation =
                new InputItemHandler(inventory, 0, inputSlots, this::isInputValid, false);
        outputHandler = new OutputItemHandler(inventory, inputSlots, outputSlots);
        upgradeHandler =
                new UpgradeItemHandler(inventory, inputSlots + outputSlots, upgradeSlots, this);
        combinedInventory = Lazy.of(
                () -> new CombinedInvWrapper(inputHandlerPlayer, outputHandler, upgradeHandler));

        this.autoEject = true;
        this.autoInsert = true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBT_TAG_INVENTORY, inventory.serializeNBT());
        tag.putBoolean(NBT_TAG_AUTO_INSERT, autoInsert);
        tag.putBoolean(NBT_TAG_AUTO_EJECT, autoEject);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NBT_TAG_INVENTORY)) {
            inventory.deserializeNBT(tag.getCompound(NBT_TAG_INVENTORY));
        }
        if (tag.contains(NBT_TAG_AUTO_INSERT)) {
            autoInsert = tag.getBoolean(NBT_TAG_AUTO_INSERT);
        }
        if (tag.contains(NBT_TAG_AUTO_EJECT)) {
            autoEject = tag.getBoolean(NBT_TAG_AUTO_EJECT);
        }
        applyUpgrades();
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
        return combinedInventory.get();
    }

    public InputItemHandler getInputHandler(Direction direction, boolean player) {
        return player ? inputHandlerPlayer : inputHandlerAutomation;
    }

    public OutputItemHandler getOutputHandler(Direction direction) {
        return outputHandler;
    }

    public UpgradeItemHandler getUpgradeHandler(Direction direction) {
        return upgradeHandler;
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

    /**
     * Reset whatever values the upgrades have changed to their default values.
     */
    abstract protected void resetUpgrades();

    /**
     * Install the given upgrade to the block entity. This should check the type of the upgrade and
     * modify the block entity accordingly.
     * 
     * @param upgrade the upgrade to install
     */
    abstract protected void installUpgrade(UpgradeItem upgrade);

    /**
     * Apply the upgrades to the block entity. This should be called whenever the upgrades are
     * changed.
     */
    @Override
    public final void applyUpgrades() {
        if (level != null && level.isClientSide()) {
            return;
        }
        resetUpgrades();
        for (int i = upgradeHandler.getStartSlot(); i < upgradeHandler.getEndSlot(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.getItem() instanceof UpgradeItem upgrade) {
                installUpgrade(upgrade);
            }
        }
    }

    public boolean isAutoEject() {
        return autoEject;
    }

    public void setAutoEject(boolean autoEject) {
        this.autoEject = autoEject;
    }

    public boolean isAutoInsert() {
        return autoInsert;
    }

    public void setAutoInsert(boolean autoInsert) {
        this.autoInsert = autoInsert;
    }

    /**
     * Push items from the output slots to adjacent inventories.
     * 
     * @param pos the position whose neighbors to push to
     */
    public void ejectOutput(BlockPos pos) {
        if (!autoEject) {
            return;
        }
        // Check if the output slots are empty
        boolean empty = true;
        for (int i = outputHandler.getStartSlot(); i < outputHandler.getEndSlot(); i++) {
            if (!outputHandler.getStackInSlot(i).isEmpty()) {
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
            if (handler == null || handler == this.getItemHandler(direction)) {
                continue;
            }
            for (int i = outputHandler.getStartSlot(); i < outputHandler.getEndSlot(); i++) {
                ItemStack extractedStack = outputHandler.extractItem(i, IO_SPEED, true);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                for (int j = 0; j < handler.getSlots(); j++) {
                    ItemStack remainder = handler.insertItem(j, extractedStack, false);
                    outputHandler.extractItem(i, extractedStack.getCount() - remainder.getCount(),
                            false);
                    if (remainder.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Pull items from adjacent inventories to the input slots.
     * 
     * @param pos the position whose neighbors to pull from
     */
    public void pullInput(BlockPos pos) {
        if (!autoInsert) {
            return;
        }
        for (Direction direction : Direction.values()) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            if (handler == null || handler == this.getItemHandler(direction)) {
                continue;
            }
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack extractedStack = handler.extractItem(i, IO_SPEED, true);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                for (int j = inputHandlerAutomation.getStartSlot(); j < inputHandlerAutomation
                        .getEndSlot(); j++) {
                    ItemStack remainder =
                            inputHandlerAutomation.insertItem(j, extractedStack, false);
                    handler.extractItem(i, extractedStack.getCount() - remainder.getCount(), false);
                    if (remainder.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }
}
