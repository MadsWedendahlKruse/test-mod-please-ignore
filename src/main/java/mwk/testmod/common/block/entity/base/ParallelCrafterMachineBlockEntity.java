package mwk.testmod.common.block.entity.base;

import java.util.ArrayList;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block entity that can craft mutliple recipes of the same type in parallel, e.g. a furnace that
 * can smelt multiple items at the same time.
 */
public abstract class ParallelCrafterMachineBlockEntity<T extends Recipe<Container>>
        extends CrafterMachineBlockEntity<T> {

    protected ParallelCrafterMachineBlockEntity(BlockEntityType<?> type, BlockPos pos,
            BlockState state, int maxEnergy, int energyPerTick, int itemSlots, int maxProgress,
            RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, energyPerTick, itemSlots, itemSlots, maxProgress,
                recipeType, sound, soundDuration);
    }

    public final void tick() {
        if (!hasEnergy()) {
            return;
        }
        // Keep looping until we have found a recipe for each input slot.
        boolean increaseProgress = true;
        boolean recipeValid = false;
        boolean progressFinished = false;
        for (int i = 0, recipesFound = 0; i < inputSlots * inputSlots
                && recipesFound < inputSlots; i++) {
            int slot = i % inputSlots;
            Optional<RecipeHolder<T>> recipe = getCurrentRecipe(slot);
            if (recipe.isEmpty()) {
                continue;
            }
            ArrayList<Pair<Integer, Integer>> outputSlots = getOutputSlots(recipe);
            if (!outputSlots.isEmpty()) {
                recipesFound++;
                recipeValid = true;
                // Only increase progress once per tick
                if (increaseProgress) {
                    increaseProgress();
                    setChanged();
                    setWorking(true);
                    increaseProgress = false;
                }
                if (hasProgressFinished()) {
                    progressFinished = true;
                    craftItem(slot, outputSlots, recipe);
                }
            }
        }
        playSound();
        if (!recipeValid) {
            setWorking(false);
        }
        if (!recipeValid || progressFinished) {
            resetProgress();
        }
    }

    /**
     * This method should return the current recipe that can be crafted given the current inventory.
     * 
     * @param slot The index of the input slot to check the recipe for. In the parallel crafter,
     *        each input slot has a corresponding output slot with the same index.
     * @return The current recipe that can be crafted.
     */
    protected Optional<RecipeHolder<T>> getCurrentRecipe(int slot) {
        return this.level.getRecipeManager().getRecipeFor(this.recipeType,
                new SimpleContainer(this.inventory.getStackInSlot(slot)), this.level);
    }

    /**
     * This method checks if the given recipe is valid and returns the indices of output slot(s) to
     * place the result in, as well as the amount that should be placed in eachs slot. In case the
     * entire recipe output can't fit into a single slot, the output is split into multiple slots.
     * 
     * @param recipe The recipe to check.
     * @return The indices of the output slot(s) to place the result in and the amount to place in
     *         each slot.
     */
    protected ArrayList<Pair<Integer, Integer>> getOutputSlots(Optional<RecipeHolder<T>> recipe) {
        if (!recipe.isEmpty()) {
            ArrayList<Pair<Integer, Integer>> outputSlots = new ArrayList<>();
            ItemStack result = recipe.get().value().getResultItem(null);
            int recipeCount = result.getCount();
            int currentCount = 0;
            for (int i = 0; i < inputSlots; i++) {
                int slot = i + inputSlots;
                if (!inventory.getStackInSlot(slot).isEmpty()
                        && !inventory.getStackInSlot(slot).is(result.getItem())) {
                    continue;
                }
                int remainingSpace =
                        inventory.getSlotLimit(slot) - inventory.getStackInSlot(slot).getCount();
                int amountToPlace = Math.min(remainingSpace, recipeCount - currentCount);
                if (amountToPlace <= 0) {
                    continue;
                }
                currentCount += amountToPlace;
                outputSlots.add(Pair.of(slot, amountToPlace));
                // Only return the output slots if we have enough slots to place the entire result
                if (currentCount >= recipeCount) {
                    return outputSlots;
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Craft the item for the given recipe and input slot.
     * 
     * @param inputSlot The index of the input slot to craft the item for.
     * @param outputSlot The indices of the output slot(s) to place the result in.
     * @param recipe The recipe to craft.
     */
    protected void craftItem(int inputSlot, ArrayList<Pair<Integer, Integer>> outputSlots,
            Optional<RecipeHolder<T>> recipe) {
        ItemStack result = recipe.get().value().getResultItem(null);
        this.inventory.extractItem(inputSlot, 1, false);
        for (Pair<Integer, Integer> outputSlot : outputSlots) {
            int newSize = inventory.getStackInSlot(outputSlot.getLeft()).getCount()
                    + outputSlot.getRight();
            this.inventory.setStackInSlot(outputSlot.getLeft(),
                    new ItemStack(result.getItem(), newSize));
        }
    }
}
