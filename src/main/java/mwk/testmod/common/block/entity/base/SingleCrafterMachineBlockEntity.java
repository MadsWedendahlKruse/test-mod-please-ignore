package mwk.testmod.common.block.entity.base;

import java.util.Optional;
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
 * A block entity that can craft a single recipe at a time, e.g. a furnace.
 */
public abstract class SingleCrafterMachineBlockEntity<T extends Recipe<Container>>
        extends CrafterMachineBlockEntity<T> {

    protected SingleCrafterMachineBlockEntity(BlockEntityType<?> type, BlockPos pos,
            BlockState state, int maxEnergy, int energyPerTick, int inputSlots, int outputSlots,
            int maxProgress, RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, energyPerTick, inputSlots, outputSlots, maxProgress,
                recipeType, sound, soundDuration);
    }

    public final void tick() {
        if (!hasEnergy()) {
            return;
        }
        Optional<RecipeHolder<T>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            setWorking(false);
            return;
        }
        if (isRecipeValid(recipe)) {
            increaseProgress();
            setWorking(true);
            setChanged();
            if (hasProgressFinished()) {
                craftItem(recipe);
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    /**
     * This method should return the current recipe that can be crafted given the current inventory.
     * 
     * @return The current recipe that can be crafted.
     */
    protected Optional<RecipeHolder<T>> getCurrentRecipe() {
        SimpleContainer container = new SimpleContainer(this.inputSlots);
        for (int i = 0; i < this.inputSlots; i++) {
            container.setItem(i, this.inventory.getStackInSlot(i));
        }
        return this.level.getRecipeManager().getRecipeFor(this.recipeType, container, level);
    }

    /**
     * This method checks if the given recipe is valid. This method is also responsible for checking
     * that the result of the recipe can be inserted into the output slot(s).
     * 
     * @param recipe The recipe to check.
     * @return True if the recipe is valid, false otherwise.
     */
    protected boolean isRecipeValid(Optional<RecipeHolder<T>> recipe) {
        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().value().getResultItem(null);
        return canInsertItemIntoSlot(inputSlots, result.getItem(), result.getCount());
    }

    /**
     * This method is responsible for crafting the item. It should also handle the removal of the
     * input items and the insertion of the output items.
     * 
     * @param recipe The recipe to craft.
     */
    protected void craftItem(Optional<RecipeHolder<T>> recipe) {
        ItemStack result = recipe.get().value().getResultItem(null);
        for (int i = 0; i < inputSlots; i++) {
            this.inventory.extractItem(i, 1, false);
        }
        this.inventory.setStackInSlot(inputSlots, new ItemStack(result.getItem(),
                this.inventory.getStackInSlot(inputSlots).getCount() + result.getCount()));
    }
}
