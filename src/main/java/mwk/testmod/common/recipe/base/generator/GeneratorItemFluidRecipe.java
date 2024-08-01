package mwk.testmod.common.recipe.base.generator;

import mwk.testmod.common.recipe.base.ItemFluidRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class GeneratorItemFluidRecipe extends ItemFluidRecipe implements GeneratorRecipe {

    private final ItemStack inputItem;
    private final FluidStack inputFluid;
    private final int energy;

    public GeneratorItemFluidRecipe(ItemStack inputItem, FluidStack inputFluid, int energy) {
        this.inputItem = inputItem;
        this.inputFluid = inputFluid;
        this.energy = energy;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess arg0) {
        return ItemStack.EMPTY;
    }

    @Override
    public FluidStack getFluidResult() {
        return FluidStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(inputItem));
        return ingredients;
    }

    @Override
    public NonNullList<FluidStack> getFluidIngredients() {
        NonNullList<FluidStack> ingredients = NonNullList.create();
        ingredients.add(inputFluid);
        return ingredients;
    }

    public ItemStack getInputItem() {
        return inputItem;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }
}
