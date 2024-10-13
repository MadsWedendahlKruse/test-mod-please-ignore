package mwk.testmod.common.recipe.base.crafter;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public abstract class OneInputItemStackRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient inputItem;

    protected OneInputItemStackRecipe(Ingredient inputItem) {
        this.inputItem = inputItem;
    }

    @Override
    public boolean canCraftInDimensions(int arg0, int arg1) {
        return true;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return inputItem.test(input.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(inputItem);
        return ingredients;
    }

    public Ingredient getInputItem() {
        return inputItem;
    }
}
