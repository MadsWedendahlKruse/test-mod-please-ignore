package mwk.testmod.common.recipe.base.generator;

import mwk.testmod.common.recipe.base.crafter.OneInputItemStackRecipe;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public abstract class GeneratorItemRecipe extends OneInputItemStackRecipe implements
        GeneratorRecipe {

    private final int energy;

    public GeneratorItemRecipe(Ingredient input, int energy) {
        super(input);
        this.energy = energy;
    }

    public int getEnergy() {
        return energy;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(Provider provider) {
        return ItemStack.EMPTY;
    }
}
