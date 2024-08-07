package mwk.testmod.common.recipe.base;

import mwk.testmod.common.util.inventory.SimpleItemFluidContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class FluidRecipe implements Recipe<Container> {

    @Override
    public boolean canCraftInDimensions(int arg0, int arg1) {
        return true;
    }

    @Override
    public boolean matches(Container container, Level level) {

        // Random — 04/14/2024 8:53 PM
        // you definitely need to implement match in a way that takes into account everything you
        // need if you're going to use the recipe manager at all, fortunately there's a generic
        // container type parameter that lets you add extra methods to container to give you access
        // to a fluid handler or something

        // Shadows — 04/14/2024 8:53 PM
        // you don't need to implement match whatsoever
        // It just means you can't use RecipeManager#getRecipeFor

        // Teamy — 04/14/2024 8:55 PM
        // why not? getRecipeFor uses it, unless you want your own lookup

        // Shadows — 04/14/2024 8:55 PM
        // doing your own lookup is fairly trivial

        // if (level.isClientSide()) {
        // return false;
        // }
        // return input.test(container.getItem(0));

        if (level.isClientSide()) {
            return false;
        }
        if (!(container instanceof SimpleItemFluidContainer fluidContainer)) {
            return false;
        }
        // TODO: Could also leave this method abstract and implement it in the subclasses
        for (int i = 0; i < getFluidIngredients().size(); i++) {
            FluidStack ingredient = getFluidIngredients().get(i);
            FluidStack containerFluid = fluidContainer.getFluid(i);
            if (containerFluid == null) {
                return false;
            }
            if (containerFluid.isEmpty()) {
                return ingredient.isEmpty();
            }
            if (!ingredient.is(containerFluid.getFluid())
                    || ingredient.getAmount() > containerFluid.getAmount()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    public abstract NonNullList<FluidStack> getFluidIngredients();

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    public abstract FluidStack getFluidResult();

}
