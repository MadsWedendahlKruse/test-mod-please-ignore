package mwk.testmod.common.recipe.base.crafter;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public abstract class OneToOneItemStackRecipe extends OneInputItemStackRecipe {

    private final ItemStack output;

    public OneToOneItemStackRecipe(Ingredient input, ItemStack output) {
        super(input);
        this.output = output;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

}
