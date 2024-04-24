package mwk.testmod.common.recipe;

import mwk.testmod.common.recipe.base.OneToOneItemStackRecipe;
import mwk.testmod.init.registries.TestModRecipeSerializers;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class CrushingRecipe extends OneToOneItemStackRecipe {

    public CrushingRecipe(Ingredient input, ItemStack output) {
        super(input, output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestModRecipeSerializers.CRUSHING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TestModRecipeTypes.CRUSHING.get();
    }

}
