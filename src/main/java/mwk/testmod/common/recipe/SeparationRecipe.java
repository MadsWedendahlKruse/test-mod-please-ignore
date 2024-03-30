package mwk.testmod.common.recipe;

import java.util.List;
import mwk.testmod.common.recipe.base.OneToManyItemStackRecipe;
import mwk.testmod.init.registries.TestModRecipeSerializers;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class SeparationRecipe extends OneToManyItemStackRecipe {

    public SeparationRecipe(Ingredient input, List<ItemStack> outputs) {
        super(input, outputs);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestModRecipeSerializers.SEPARATION_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TestModRecipeTypes.SEPARATION.get();
    }

}
