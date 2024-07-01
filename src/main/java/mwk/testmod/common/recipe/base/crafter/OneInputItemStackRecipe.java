package mwk.testmod.common.recipe.base.crafter;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class OneInputItemStackRecipe implements Recipe<Container> {

    private final Ingredient input;

    protected OneInputItemStackRecipe(Ingredient input) {
        this.input = input;
    }

    @Override
    public boolean canCraftInDimensions(int arg0, int arg1) {
        return true;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return input.test(container.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(input);
        return ingredients;
    }

    public Ingredient getInput() {
        return input;
    }
}
