package mwk.testmod.common.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
    protected final Lazy<IItemHandler> itemHandler;

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
        };
        itemHandler = Lazy.of(() -> inventory);
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

    public IItemHandler getItemHandler() {
        return itemHandler.get();
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
}
