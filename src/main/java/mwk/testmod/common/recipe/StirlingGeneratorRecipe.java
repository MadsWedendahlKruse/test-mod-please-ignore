package mwk.testmod.common.recipe;

import mwk.testmod.common.recipe.base.generator.GeneratorItemRecipe;
import mwk.testmod.init.registries.TestModRecipeSerializers;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class StirlingGeneratorRecipe extends GeneratorItemRecipe {

    public StirlingGeneratorRecipe(Ingredient input, int energy) {
        super(input, energy);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestModRecipeSerializers.STIRLING_GENERATOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TestModRecipeTypes.STIRLING_GENERATOR.get();
    }

}
