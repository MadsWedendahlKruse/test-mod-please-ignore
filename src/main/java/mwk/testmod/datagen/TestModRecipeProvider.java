package mwk.testmod.datagen;

import java.util.concurrent.CompletableFuture;
import mwk.testmod.TestMod;
import mwk.testmod.common.recipe.CrushingRecipe;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

public class TestModRecipeProvider extends RecipeProvider {

        public TestModRecipeProvider(PackOutput packOutput,
                        CompletableFuture<Provider> lookupProvider) {
                super(packOutput, lookupProvider);
        }

        @Override
        protected void buildRecipes(RecipeOutput recipeOutput) {
                recipeOutput.accept(new ResourceLocation(TestMod.MODID, "redstone_block_to_dust"),
                                new CrushingRecipe(Ingredient.of(Blocks.REDSTONE_BLOCK),
                                                new ItemStack(Items.REDSTONE, 9)),
                                null);
                recipeOutput.accept(new ResourceLocation(TestMod.MODID, "lapis_block_to_dust"),
                                new CrushingRecipe(Ingredient.of(Blocks.LAPIS_BLOCK),
                                                new ItemStack(Items.LAPIS_LAZULI, 9)),
                                null);
        }

}
