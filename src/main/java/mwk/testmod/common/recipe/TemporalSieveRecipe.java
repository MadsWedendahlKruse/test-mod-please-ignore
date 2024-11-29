package mwk.testmod.common.recipe;

import mwk.testmod.common.recipe.base.generator.GeneratorItemRecipe;
import mwk.testmod.init.registries.TestModRecipeSerializers;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class TemporalSieveRecipe extends GeneratorItemRecipe {

    public TemporalSieveRecipe(Ingredient input, int energy) {
        super(input, energy);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestModRecipeSerializers.TEMPORAL_SIEVE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TestModRecipeTypes.TEMPORAL_SIEVE.get();
    }
}
