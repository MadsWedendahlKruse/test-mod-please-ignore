package mwk.testmod.common.block.entity.modules;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * A module that processes recipes. TODO: Explain
 *
 * @param <I> The type of the recipe input.
 * @param <T> The type of the recipe.
 */
public class ProcessingModule<I extends RecipeInput, T extends Recipe<I>> implements MachineModule {

    public static final String NBT_TAG_PROGRESS = "progress";

    private final MachineBlockEntity machine;

    protected final RecipeType<T> recipeType;
    // Cache recipe to avoid looking it up every tick
    protected I lastInput;
    protected T lastRecipe;

    protected int progress;
    protected int maxProgress;
    // Storing the progress per tick as a float makes applying upgrades easier
    protected float progressPerTick;
    protected int resourcePerTick;
    // Base values before upgrades
    public final int maxProgressBase;
    public final int resourcePerTickBase;

    // Functions to interact with the machine (explained in the constructor)
    private Supplier<Boolean> hasResource;
    private Runnable consumeResource;
    private Supplier<I> getRecipeInput;
    private BiFunction<I, I, Boolean> isRecipeInputEqual;
    private Function<T, Boolean> canProcessRecipe;
    private Consumer<T> processRecipe;

    /**
     * Create a new processing module.
     *
     * @param machine            The machine block entity that this module is attached to.
     * @param recipeType         The type of the recipe that this module can process.
     * @param maxProgress        The number of ticks it takes to process a recipe.
     * @param resourcePerTick    The amount of resource consumed per tick.
     * @param hasResource        A function that returns true if the machine has the required
     *                           resources to process the recipe.
     * @param consumeResource    A function that consumes the resources required to process the
     *                           recipe.
     * @param getRecipeInput     A function that returns the recipe input that can be used to look
     *                           up the recipe in the recipe manager.
     * @param isRecipeInputEqual A function that returns true if the two inputs are the same. This
     *                           is used to avoid looking up the recipe every tick.
     * @param canProcessRecipe   A function that returns true if the machine can process the recipe.
     *                           This is used to check if the outputs can be inserted into the
     *                           output slots and tanks.
     * @param processRecipe      A function that processes the recipe, i.e. removes the input items
     *                           and fluids and inserts the output items and fluids.
     */
    public ProcessingModule(MachineBlockEntity machine, RecipeType<T> recipeType, int maxProgress,
            int resourcePerTick, Supplier<Boolean> hasResource, Runnable consumeResource,
            Supplier<I> getRecipeInput, BiFunction<I, I, Boolean> isRecipeInputEqual,
            Function<T, Boolean> canProcessRecipe, Consumer<T> processRecipe) {
        this.machine = machine;
        this.recipeType = recipeType;
        this.maxProgress = maxProgress;
        this.resourcePerTick = resourcePerTick;
        this.maxProgressBase = maxProgress;
        this.resourcePerTickBase = resourcePerTick;
        this.progressPerTick = 1.0F;
        this.hasResource = hasResource;
        this.consumeResource = consumeResource;
        this.getRecipeInput = getRecipeInput;
        this.isRecipeInputEqual = isRecipeInputEqual;
        this.canProcessRecipe = canProcessRecipe;
        this.processRecipe = processRecipe;

    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registries) {
        tag.putInt(NBT_TAG_PROGRESS, progress);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        progress = tag.getInt(NBT_TAG_PROGRESS);
    }

    public boolean hasResource() {
        return hasResource.get();
    }

    public void consumeResource() {
        consumeResource.run();
    }

    /**
     * @return The current recipe that can be crafted given the current inputs.
     */
    public T getCurrentRecipe() {
        Level level = machine.getLevel();
        // Check if the input has changed since the last recipe lookup
        I recipeInput = getRecipeInput.get();
        if (lastInput != null && isRecipeInputEqual.apply(lastInput, recipeInput)) {
            return lastRecipe;
        }
        // Check if the input items and fluids match the latest recipe
        if (lastRecipe != null && lastRecipe.matches(recipeInput, level)) {
            return lastRecipe;
        }
        // Look up a new recipe
        lastRecipe = level.getRecipeManager().getRecipeFor(this.recipeType, recipeInput, level)
                .map(RecipeHolder::value).orElse(null);
        TestMod.LOGGER.debug("Got new recipe from recipe manager: " + lastRecipe);
        lastInput = recipeInput;
        return lastRecipe;
    }

    public boolean canProcessRecipe(T recipe) {
        return canProcessRecipe.apply(recipe);
    }

    public void processRecipe(T recipe) {
        processRecipe.accept(recipe);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void resetProgress() {
        progress = 0;
    }

    public void increaseProgress() {
        progress++;
    }

    public boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    public float getProgressPerTick() {
        return progressPerTick;
    }

    public void setProgressPerTick(float progressPerTick) {
        this.progressPerTick = progressPerTick;
    }

    public int getResourcePerTick() {
        return resourcePerTick;
    }

    public void setResourcePerTick(int resourcePerTick) {
        this.resourcePerTick = resourcePerTick;
    }

    public T getLastRecipe() {
        return lastRecipe;
    }

    public void setLastRecipe(T lastRecipe) {
        this.lastRecipe = lastRecipe;
    }

    public void clearInputCache() {
        lastInput = null;
    }

    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

}
