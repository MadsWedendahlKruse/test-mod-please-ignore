package mwk.testmod.common.recipe;

import mwk.testmod.common.recipe.base.crafter.CatalystItemStackRecipe;
import mwk.testmod.init.registries.TestModRecipeSerializers;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class StampingRecipe extends CatalystItemStackRecipe {

    public StampingRecipe(Ingredient catalyst, Ingredient input, ItemStack output) {
        super(catalyst, input, output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestModRecipeSerializers.STAMPING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TestModRecipeTypes.STAMPING.get();
    }
}
