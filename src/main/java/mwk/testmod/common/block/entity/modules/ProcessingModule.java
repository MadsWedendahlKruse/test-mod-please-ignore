package mwk.testmod.common.block.entity.modules;

import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.util.inventory.SimpleFluidContainer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

public class ProcessingModule<I extends RecipeInput, T extends Recipe<I>> implements MachineModule {

    public static final String NBT_TAG_PROGRESS = "progress";

    private final MachineBlockEntity machine;

    protected final RecipeType<T> recipeType;
    // We only need to look up the recipe if the input slots/tank have changed
    protected Container latestItemInputs;
    protected SimpleFluidContainer latestFluidInputs;
    protected T latestRecipe;

    protected int progress;
    protected int maxProgress;
    // Storing the progress per tick as a float makes applying upgrades easier
    protected float progressPerTick;
    protected int energyPerTick;
    // Base values before upgrades
    public final int maxProgressBase;
    public final int energyPerTickBase;

    public ProcessingModule(MachineBlockEntity machine, RecipeType<T> recipeType, int maxProgress,
            int energyPerTick) {
        this.machine = machine;
        this.recipeType = recipeType;
        this.maxProgress = maxProgress;
        this.energyPerTick = energyPerTick;
        this.maxProgressBase = maxProgress;
        this.energyPerTickBase = energyPerTick;
        this.progressPerTick = 1.0F;
    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registries) {
        tag.putInt(NBT_TAG_PROGRESS, progress);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
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
