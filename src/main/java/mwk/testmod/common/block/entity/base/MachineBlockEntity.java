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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
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
    public static final String NBT_TAG_AUTO_INSERT = "autoInsert";
    public static final String NBT_TAG_AUTO_EJECT = "autoEject";

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

    private boolean autoInsert;
    private boolean autoEject;

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, EnergyType energyType, int inputSlots, int outputSlots, int upgradeSlots,
            int[] inputTankCapacities, int[] outputTankCapacities) {
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
                if (inputItemHandlerPlayer.isSlotValid(slot)) {
                    return inputItemHandlerPlayer.getSlotLimit(slot);
                }
                if (outputItemHandler.isSlotValid(slot)) {
                    return outputItemHandler.getSlotLimit(slot);
                }
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
        for (int i = 0; i < inputTanks; i++) {
            tankCapacities[i] = inputTankCapacities[i];
        }
        for (int i = 0; i < outputTanks; i++) {
            tankCapacities[inputTanks + i] = outputTankCapacities[i];
        }
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

        this.autoEject = true;
        this.autoInsert = true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBT_TAG_INVENTORY, inventory.serializeNBT());
        tag.put(NBT_TAG_FLUID_TANKS, fluidTanks.serializeNBT());
        tag.putBoolean(NBT_TAG_AUTO_INSERT, autoInsert);
        tag.putBoolean(NBT_TAG_AUTO_EJECT, autoEject);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NBT_TAG_INVENTORY)) {
            inventory.deserializeNBT(tag.getCompound(NBT_TAG_INVENTORY));
        }
        if (tag.contains(NBT_TAG_FLUID_TANKS)) {
            fluidTanks.deserializeNBT(tag.getCompound(NBT_TAG_FLUID_TANKS));
        }
        if (tag.contains(NBT_TAG_AUTO_INSERT)) {
            autoInsert = tag.getBoolean(NBT_TAG_AUTO_INSERT);
        }
        if (tag.contains(NBT_TAG_AUTO_EJECT)) {
            autoEject = tag.getBoolean(NBT_TAG_AUTO_EJECT);
        }
        applyUpgrades();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (inputTanks + outputTanks > 0) {
            tag.put(NBT_TAG_FLUID_TANKS, fluidTanks.serializeNBT());
        }
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundTag tag = pkt.getTag();
        if (tag == null) {
            return;
        }
        if (tag.contains(NBT_TAG_FLUID_TANKS)) {
            fluidTanks.deserializeNBT(tag.getCompound(NBT_TAG_FLUID_TANKS));
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
     * @param tank the tank
     * @param stack the stack
     * @return true if the stack can be inserted, false otherwise
     */
    protected boolean isInputFluidValid(int tank, FluidStack stack) {
        return true;
    }

    public IFluidHandler getInputFluidHandler(Direction direction) {
        return inputFluidHandler;
    }

    public IFluidHandler getOutputFluidHandler(Direction direction) {
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
        for (int i = upgradeItemHandler.getStartSlot(); i < upgradeItemHandler.getEndSlot(); i++) {
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
    public void ejectItemOutput(BlockPos pos) {
        if (!autoEject) {
            return;
        }
        // Check if the output slots are empty
        boolean empty = true;
        for (int i = outputItemHandler.getStartSlot(); i < outputItemHandler.getEndSlot(); i++) {
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
            if (handler == null || handler == this.getItemHandler(direction)) {
                continue;
            }
            for (int i = outputItemHandler.getStartSlot(); i < outputItemHandler
                    .getEndSlot(); i++) {
                ItemStack extractedStack = outputItemHandler.extractItem(i, ITEM_IO_SPEED, true);
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

    /**
     * Pull items from adjacent inventories to the input slots.
     * 
     * @param pos the position whose neighbors to pull from
     */
    public void pullItemInput(BlockPos pos) {
        if (!autoInsert) {
            return;
        }
        for (Direction direction : Direction.values()) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            // Don't pull from itself
            if (handler == null || handler == this.getItemHandler(direction)) {
                continue;
            }
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack extractedStack = handler.extractItem(i, ITEM_IO_SPEED, true);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                for (int j = inputItemHandlerAutomation
                        .getStartSlot(); j < inputItemHandlerAutomation.getEndSlot(); j++) {
                    ItemStack remainder =
                            inputItemHandlerAutomation.insertItem(j, extractedStack, false);
                    handler.extractItem(i, extractedStack.getCount() - remainder.getCount(), false);
                    if (remainder.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Push fluids from the output tanks to adjacent tanks.
     * 
     * @param pos the position whose neighbors to push to
     */
    public void ejectFluidOutput(BlockPos pos) {
        if (!autoEject) {
            return;
        }
        // Check if the output tanks are empty
        boolean empty = true;
        for (int i = outputFluidHandler.getStartTank(); i < outputFluidHandler.getEndTank(); i++) {
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
            if (handler == null || handler == this.getOutputFluidHandler(direction)) {
                continue;
            }
            for (int i = outputFluidHandler.getStartTank(); i < outputFluidHandler
                    .getEndTank(); i++) {
                FluidStack extractedStack =
                        outputFluidHandler.drain(FLUID_IO_SPEED, FluidAction.SIMULATE);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                int received = handler.fill(extractedStack, FluidAction.EXECUTE);
                outputFluidHandler.drain(received, FluidAction.EXECUTE);
            }
        }
    }

    /**
     * Pull fluids from adjacent tanks to the input tanks.
     * 
     * @param pos the position whose neighbors to pull from
     */
    public void pullFluidInput(BlockPos pos) {
        if (!autoInsert) {
            return;
        }
        for (Direction direction : Direction.values()) {
            IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            // Don't pull from itself
            if (handler == null || handler == this.getInputFluidHandler(direction)) {
                continue;
            }
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack extractedStack = handler.drain(FLUID_IO_SPEED, FluidAction.SIMULATE);
                if (extractedStack.isEmpty()) {
                    continue;
                }
                int received = inputFluidHandler.fill(extractedStack, FluidAction.EXECUTE);
                handler.drain(received, FluidAction.EXECUTE);
            }
        }
    }
}
