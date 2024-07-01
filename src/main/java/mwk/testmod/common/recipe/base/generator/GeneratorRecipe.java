package mwk.testmod.common.recipe.base.generator;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class GeneratorRecipe implements Recipe<Container> {

    private final Ingredient input;
    private final int energy;

    public GeneratorRecipe(Ingredient input, int energy) {
        this.input = input;
        this.energy = energy;
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

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    public Ingredient getInput() {
        return input;
    }

    public int getEnergy() {
        return energy;
    }
}
