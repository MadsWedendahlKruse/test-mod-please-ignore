package mwk.testmod.common.recipe.base.crafter;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class CatalystItemStackRecipe implements Recipe<Container> {

    private final Ingredient catalyst;
    private final Ingredient input;
    private final ItemStack output;

    protected CatalystItemStackRecipe(Ingredient catalyst, Ingredient input, ItemStack output) {
        this.catalyst = catalyst;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        return catalyst.test(pContainer.getItem(0)) && input.test(pContainer.getItem(1));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(catalyst);
        ingredients.add(input);
        return ingredients;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}