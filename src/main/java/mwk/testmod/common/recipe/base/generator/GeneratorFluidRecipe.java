package mwk.testmod.common.recipe.base.generator;

import mwk.testmod.common.recipe.base.FluidRecipe;
import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class GeneratorFluidRecipe extends FluidRecipe implements GeneratorRecipe {

    private final FluidStack input;
    private final int energy;

    public GeneratorFluidRecipe(FluidStack input, int energy) {
        this.input = input;
        this.energy = energy;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public FluidStack getFluidResult() {
        return FluidStack.EMPTY;
    }

    @Override
    public NonNullList<FluidStack> getFluidIngredients() {
        NonNullList<FluidStack> ingredients = NonNullList.create();
        ingredients.add(input);
        return ingredients;
    }

    public FluidStack getInput() {
        return input;
    }
}
