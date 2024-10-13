package mwk.testmod.common.recipe.base.crafter;

import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public abstract class OneToManyItemStackRecipe extends OneInputItemStackRecipe {

    private final List<ItemStack> outputs;

    public OneToManyItemStackRecipe(Ingredient input, List<ItemStack> outputs) {
        super(input);
        this.outputs = outputs;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

}
