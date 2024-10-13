package mwk.testmod.common.block.entity.base;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.interfaces.IDescripable;
import mwk.testmod.common.block.interfaces.IUpgradable;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.common.util.inventory.handler.FluidStackHandler;
import mwk.testmod.common.util.inventory.handler.InputFluidHandler;
import mwk.testmod.common.util.inventory.handler.InputItemHandler;
import mwk.testmod.common.util.inventory.handler.OutputFluidHandler;
import mwk.testmod.common.util.inventory.handler.OutputItemHandler;
import mwk.testmod.common.util.inventory.handler.UpgradeItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

/**
 * A block entity that stores energy and also has an inventory.
 */
public abstract class MachineBlockEntity extends EnergyBlockEntity
        implements MenuProvider, IUpgradable, IDescripable {

    public static final String NBT_TAG_INVENTORY = "inventory";
    public static final String NBT_TAG_FLUID_TANKS = "fluidTanks";
    public static final String NBT_TAG_AUTO_PULL = "autoPull";
    public static final String NBT_TAG_AUTO_PUSH = "autoPush";

    public static final int ITEM_IO_SPEED = TestModConfig.MACHINE_ITEM_IO_SPEED_DEFAULT.get(); // [items/tick]
    public static final int FLUID_IO_SPEED = TestModConfig.MACHINE_FLUID_IO_SPEED_DEFAULT.get(); // [mB/tick]

    protected final int inputSlots;
    protected final int outputSlots;
    protected final int upgradeSlots;
    protected final int inventorySize;

    protected final ItemStackHandler inventory;
    protected final InputItemHandler inputItemHandlerPlayer;
    protected final InputItemHandler inputItemHandlerAutomation;
    protected final OutputItemHandler outputItemHandler;
    protected final UpgradeItemHandler upgradeItemHandler;
    protected final Lazy<CombinedInvWrapper> combinedInventory;

    protected static final int[] EMPTY_TANKS = new int[0];

    protected final int inputTanks;
    protected final int outputTanks;

    protected final FluidStackHandler fluidTanks;
    protected final InputFluidHandler inputFluidHandler;
    protected final OutputFluidHandler outputFluidHandler;

    private boolean autoPull;
    private boolean autoPush;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, EnergyType energyType, int inputSlots, int outputSlots, int upgradeSlots,
            int[] inputTankCapacities, int[] outputTankCapacities) {
        super(type, pos, state, maxEnergy, energyType);
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.upgradeSlots = upgradeSlots;
        inventorySize = inputSlots + outputSlots + upgradeSlots;
        inventory = new ItemStackHandler(inventorySize) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                onInventoryChanged(slot);
            }

            @Override
            public int getSlotLimit(int slot) {
//                if (inputItemHandlerPlayer.isSlotValid(slot)) {
//                    return inputItemHandlerPlayer.getSlotLimit(slot);
//                }
//                if (outputItemHandler.isSlotValid(slot)) {
//                    return outputItemHandler.getSlotLimit(slot);
//                }
                if (upgradeItemHandler.isSlotValid(slot)) {
                    return upgradeItemHandler.getSlotLimit(slot);
                }
                return super.getSlotLimit(slot);
                // TODO: For some reason this doesn't work
                // return combinedInventory.get().getSlotLimit(slot);
            }
        };
        inputItemHandlerPlayer =
                new InputItemHandler(inventory, 0, inputSlots, this::isInputItemValid, true);
        inputItemHandlerAutomation =
                new InputItemHandler(inventory, 0, inputSlots, this::isInputItemValid, false);
        outputItemHandler = new OutputItemHandler(inventory, inputSlots, outputSlots);
        upgradeItemHandler =
                new UpgradeItemHandler(inventory, inputSlots + outputSlots, upgradeSlots, this);
        combinedInventory = Lazy.of(() -> new CombinedInvWrapper(inputItemHandlerPlayer,
                outputItemHandler, upgradeItemHandler));

        inputTanks = inputTankCapacities.length;
        outputTanks = outputTankCapacities.length;
        int[] tankCapacities = new int[inputTanks + outputTanks];
        System.arraycopy(inputTankCapacities, 0, tankCapacities, 0, inputTanks);
        System.arraycopy(outputTankCapacities, 0, tankCapacities, inputTanks, outputTanks);
        fluidTanks = new FluidStackHandler(tankCapacities) {
            @Override
            protected void onContentsChanged(int tank) {
                setChanged();
                // TODO: This works, but for most cases the tank contents can only be seen in the
                // GUI, so most of these syncs are unnecessary
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(),
                        Block.UPDATE_CLIENTS);
            }
        };
        inputFluidHandler =
                new InputFluidHandler(fluidTanks, 0, inputTanks, this::isInputFluidValid);
        outputFluidHandler = new OutputFluidHandler(fluidTanks, inputTanks, outputTanks);

        this.autoPush = true;
        this.autoPull = true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(NBT_TAG_INVENTORY, inventory.serializeNBT(registries));
        tag.put(NBT_TAG_FLUID_TANKS, fluidTanks.serializeNBT(registries));
        tag.putBoolean(NBT_TAG_AUTO_PULL, autoPull);
        tag.putBoolean(NBT_TAG_AUTO_PUSH, autoPush);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(NBT_TAG_INVENTORY)) {
            inventory.deserializeNBT(registries, tag.getCompound(NBT_TAG_INVENTORY));
        }
        if (tag.contains(NBT_TAG_FLUID_TANKS)) {
            fluidTanks.deserializeNBT(registries, tag.getCompound(NBT_TAG_FLUID_TANKS));
        }
        if (tag.contains(NBT_TAG_AUTO_PULL)) {
            autoPull = tag.getBoolean(NBT_TAG_AUTO_PULL);
        }
        if (tag.contains(NBT_TAG_AUTO_PUSH)) {
            autoPush = tag.getBoolean(NBT_TAG_AUTO_PUSH);
        }
        applyUpgrades();
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (inputTanks + outputTanks > 0) {
            tag.put(NBT_TAG_FLUID_TANKS, fluidTanks.serializeNBT(registries));
        }
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
            Provider registries) {
        super.onDataPacket(net, pkt, registries);
        CompoundTag tag = pkt.getTag();
        if (tag == null) {
            return;
        }
        if (tag.contains(NBT_TAG_FLUID_TANKS)) {
            fluidTanks.deserializeNBT(registries, tag.getCompound(NBT_TAG_FLUID_TANKS));
        }
    }

    /**
     * Called when the contents of the inventory change. By default, this method does nothing, but
     * it can be overridden to provide custom behavior.
     *
     * @param slot the slot that changed
     */
    protected void onInventoryChanged(int slot) {
    }

    /**
     * Checks if the given stack can be inserted into the given slot. This is used to check if the
     * stack can be inserted into the input slots. By default, this method returns true, but it can
     * be overridden to provide custom behavior.
     *
     * @param slot  the slot
     * @param stack the stack
     * @return true if the stack can be inserted, false otherwise
     */
    protected boolean isInputItemValid(int slot, ItemStack stack) {
        return true;
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

    /**
     * Checks if the given stack can be inserted into the given tank. This is used to check if the
     * stack can be inserted into the input tanks. By default, this method returns true, but it can
     * be overridden to provide custom behavior.
     *
     * @param tank  the tank
     * @param stack the stack
     * @return true if the stack can be inserted, false otherwise
     */
    protected boolean isInputFluidValid(int tank, FluidStack stack) {
        return true;
    }

    public InputFluidHandler getInputFluidHandler(Direction direction) {
        return inputFluidHandler;
    }

    public OutputFluidHandler getOutputFluidHandler(Direction direction) {
        return outputFluidHandler;
    }

    public int getInputTanks() {
        return inputTanks;
    }

    public int getOutputTanks() {
        return outputTanks;
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
        for (int i = 0; i < upgradeItemHandler.getSlots(); i++) {
            ItemStack stack = upgradeItemHandler.getStackInSlot(i);
            if (stack.getItem() instanceof UpgradeItem upgrade) {
                installUpgrade(upgrade);
            }
        }
    }

    public boolean isAutoPush() {
        return autoPush;
    }

    public void setAutoPush(boolean autoPush) {
        this.autoPush = autoPush;
    }

    public boolean isAutoPull() {
        return autoPull;
    }

    public void setAutoPull(boolean autoPull) {
        this.autoPull = autoPull;
    }
}
