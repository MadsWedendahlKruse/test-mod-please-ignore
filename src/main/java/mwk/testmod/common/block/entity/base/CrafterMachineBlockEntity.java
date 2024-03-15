package mwk.testmod.common.block.entity.base;

import mwk.testmod.common.block.interfaces.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block entity that can craft items using recipes.
 */
public abstract class CrafterMachineBlockEntity<T extends Recipe<?>> extends BaseMachineBlockEntity
        implements ITickable, MenuProvider {

    public static final String NBT_TAG_PROGRESS = "progress";

    protected final RecipeType<T> recipeType;

    private int progress;
    private int maxProgress;
    private int energyPerTick;

    protected CrafterMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, int inputSlots, int outputSlots, int maxProgress,
            RecipeType<T> recipeType) {
        super(type, pos, state, maxEnergy, EnergyType.CONSUMER, inputSlots, outputSlots);
        this.maxProgress = maxProgress;
        this.energyPerTick = energyPerTick;
        this.recipeType = recipeType;
    }

    protected void resetProgress() {
        progress = 0;
    }

    protected void increaseProgress() {
        progress++;
        energy.extractEnergy(energyPerTick, false);
    }

    protected boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    protected boolean hasEnergy() {
        return energy.getEnergyStored() >= energyPerTick;
    }

    protected boolean canInsertItemIntoSlot(int slot, Item item, int count) {
        return inventory.getStackInSlot(slot).isEmpty() || (inventory.getStackInSlot(slot).is(item)
                && inventory.getStackInSlot(slot).getCount() + count <= inventory
                        .getSlotLimit(slot));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(NBT_TAG_PROGRESS, progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt(NBT_TAG_PROGRESS);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }
}
