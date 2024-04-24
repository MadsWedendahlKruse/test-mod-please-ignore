package mwk.testmod.common.recipe.base;

import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public abstract class OneToManyItemStackRecipe extends OneInputItemStackRecipe {

    private final List<ItemStack> outputs;

    public OneToManyItemStackRecipe(Ingredient input, List<ItemStack> outputs) {
        super(input);
        this.outputs = outputs;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

}
