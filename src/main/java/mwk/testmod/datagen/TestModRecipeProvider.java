package mwk.testmod.datagen;

import java.util.concurrent.CompletableFuture;
import mwk.testmod.TestMod;
import mwk.testmod.common.recipe.CrushingRecipe;
import mwk.testmod.common.util.TestModTags;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class TestModRecipeProvider extends RecipeProvider {

	public TestModRecipeProvider(PackOutput packOutput,
			CompletableFuture<Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		registerCrushingRecipes(recipeOutput);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TestModItems.STEEL_DUST)
				.requires(TestModTags.Items.IRON_DUST).requires(TestModTags.Items.COAL_DUST)
				.unlockedBy(getHasName(TestModItems.IRON_DUST), has(TestModTags.Items.IRON_DUST))
				.save(recipeOutput);
		SimpleCookingRecipeBuilder
				.blasting(Ingredient.of(TestModTags.Items.STEEL_DUST), RecipeCategory.MISC,
						TestModItems.STEEL_INGOT.get(), 1.0F, 200)
				.unlockedBy(getHasName(TestModItems.STEEL_DUST), has(TestModTags.Items.STEEL_DUST))
				.save(recipeOutput);
	}

	private void registerCrushingRecipe(RecipeOutput recipeOutput, String name, Ingredient input,
			ItemLike output, int count) {
		recipeOutput.accept(new ResourceLocation(TestMod.MODID, name),
				new CrushingRecipe(input, new ItemStack(output, count)), null);
	}

	private void registerCrushingRecipe(RecipeOutput recipeOutput, String name, Ingredient input,
			ItemLike output) {
		registerCrushingRecipe(recipeOutput, name, input, output, 1);
	}

	private void registerCrushingRecipes(RecipeOutput recipeOutput) {
		registerCrushingRecipe(recipeOutput, "gravel_from_cobblestone",
				Ingredient.of(Blocks.COBBLESTONE), Blocks.GRAVEL);
		registerCrushingRecipe(recipeOutput, "sand_from_gravel", Ingredient.of(Blocks.GRAVEL),
				Blocks.SAND);
		registerCrushingRecipe(recipeOutput, "raw_iron_from_iron_ore",
				Ingredient.of(Blocks.IRON_ORE), Items.RAW_IRON, 3);
		registerCrushingRecipe(recipeOutput, "raw_gold_from_gold_ore",
				Ingredient.of(Blocks.GOLD_ORE), Items.RAW_GOLD, 3);
		registerCrushingRecipe(recipeOutput, "raw_copper_from_copper_ore",
				Ingredient.of(Blocks.COPPER_ORE), Items.RAW_COPPER, 8);
		registerCrushingRecipe(recipeOutput, "redstone_dust_from_redstone_ore",
				Ingredient.of(Blocks.REDSTONE_ORE), Items.REDSTONE, 12);
		registerCrushingRecipe(recipeOutput, "redstone_dust_from_redstone_block",
				Ingredient.of(Blocks.REDSTONE_BLOCK), Items.REDSTONE, 9);
		registerCrushingRecipe(recipeOutput, "lapis_dust_from_lapis_ore",
				Ingredient.of(Blocks.LAPIS_ORE), Items.LAPIS_LAZULI, 12);
		registerCrushingRecipe(recipeOutput, "lapis_dust_from_lapis_block",
				Ingredient.of(Blocks.LAPIS_BLOCK), Items.LAPIS_LAZULI, 9);
		registerCrushingRecipe(recipeOutput, "diamond_from_diamond_ore",
				Ingredient.of(Blocks.DIAMOND_ORE), Items.DIAMOND, 2);
		registerCrushingRecipe(recipeOutput, "coal_dust_from_coal", Ingredient.of(Items.COAL),
				TestModItems.COAL_DUST);
		registerCrushingRecipe(recipeOutput, "iron_dust_from_iron_ingot",
				Ingredient.of(Items.IRON_INGOT), TestModItems.IRON_DUST);
		registerCrushingRecipe(recipeOutput, "steel_dust_from_steel_ingot",
				Ingredient.of(TestModTags.Items.STEEL_INGOT), TestModItems.STEEL_DUST);
	}
}
