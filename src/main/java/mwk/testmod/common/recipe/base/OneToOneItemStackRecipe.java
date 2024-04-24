package mwk.testmod.common.recipe.base;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public abstract class OneToOneItemStackRecipe extends OneInputItemStackRecipe {

    private final ItemStack output;

    public OneToOneItemStackRecipe(Ingredient input, ItemStack output) {
        super(input);
        this.output = output;
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

}
