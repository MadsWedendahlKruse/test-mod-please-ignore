package mwk.testmod.common.recipe.inputs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CatalystRecipeInput(ItemStack catalyst, ItemStack input) implements RecipeInput {

    @Override
    public ItemStack getItem(int i) {
        return switch (i) {
            case 0 -> catalyst;
            case 1 -> input;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
