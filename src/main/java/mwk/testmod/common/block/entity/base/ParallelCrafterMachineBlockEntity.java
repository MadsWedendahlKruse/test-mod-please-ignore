package mwk.testmod.common.block.entity.base;

import java.util.Optional;
import net.minecraft.core.BlockPos;
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
            RecipeType<T> recipeType) {
        super(type, pos, state, maxEnergy, energyPerTick, itemSlots, itemSlots, maxProgress,
                recipeType);
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
            if (isRecipeValid(slot, recipe)) {
                recipesFound++;
                recipeValid = true;
                if (increaseProgress) {
                    increaseProgress();
                    setChanged();
                    increaseProgress = false;
                }
                if (hasProgressFinished()) {
                    progressFinished = true;
                    craftItem(slot, recipe);
                }
            }
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
     * This method checks if the given recipe is valid. This method is also responsible for checking
     * that the result of the recipe can be inserted into the output slot(s).
     * 
     * @param slot The index of the output slot to check if the recipe is valid for. In the parallel
     *        crafter, each output slot has a corresponding input slot with the same index.
     * @param recipe The recipe to check.
     * @return True if the recipe is valid, false otherwise.
     */
    protected boolean isRecipeValid(int slot, Optional<RecipeHolder<T>> recipe) {
        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().value().getResultItem(null);
        return canInsertItemIntoSlot(inputSlots + slot, result.getItem(), result.getCount());
    }

    /**
     * Craft the item for the given recipe and insert the result into the output slot.
     * 
     * @param slot The index of the output slot to place the result in. In the parallel crafter,
     *        each output slot has a corresponding input slot with the same index.
     * @param recipe The recipe to craft.
     */
    protected void craftItem(int slot, Optional<RecipeHolder<T>> recipe) {
        ItemStack result = recipe.get().value().getResultItem(null);
        this.inventory.extractItem(slot, 1, false);
        this.inventory.setStackInSlot(inputSlots + slot, new ItemStack(result.getItem(),
                this.inventory.getStackInSlot(inputSlots + slot).getCount() + result.getCount()));
    }
}
