package mwk.testmod.common.recipe.inputs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidRecipeInput(FluidStack input) implements RecipeInput {

    @Override
    public ItemStack getItem(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }

    public FluidStack getFluid(int i) {
        return input;
    }
}
