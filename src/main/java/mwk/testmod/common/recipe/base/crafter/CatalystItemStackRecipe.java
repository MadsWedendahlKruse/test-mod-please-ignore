package mwk.testmod.common.recipe.base.crafter;

import mwk.testmod.common.recipe.inputs.CatalystRecipeInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class CatalystItemStackRecipe implements Recipe<CatalystRecipeInput> {

    private final Ingredient catalystItem;
    private final Ingredient inputItem;
    private final ItemStack outputItem;

    protected CatalystItemStackRecipe(Ingredient catalystItem, Ingredient inputItem,
            ItemStack outputItem) {
        this.catalystItem = catalystItem;
        this.inputItem = inputItem;
        this.outputItem = outputItem;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public boolean matches(CatalystRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return catalystItem.test(input.getItem(0)) && inputItem.test(input.getItem(1));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(catalystItem);
        ingredients.add(inputItem);
        return ingredients;
    }

    @Override
    public ItemStack assemble(CatalystRecipeInput input, HolderLookup.Provider registries) {
        return outputItem.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return outputItem.copy();
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    public Ingredient getCatalystItem() {
        return catalystItem;
    }

    public Ingredient getInputItem() {
        return inputItem;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }
}