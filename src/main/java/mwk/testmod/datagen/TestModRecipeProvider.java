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
		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "gravel_from_cobblestone"),
				new CrushingRecipe(Ingredient.of(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL)),
				null);
		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "sand_from_gravel"),
				new CrushingRecipe(Ingredient.of(Blocks.GRAVEL), new ItemStack(Blocks.SAND)), null);

		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "raw_iron_from_iron_ore"),
				new CrushingRecipe(Ingredient.of(Blocks.IRON_ORE),
						new ItemStack(Items.RAW_IRON, 3)),
				null);
		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "raw_gold_from_gold_ore"),
				new CrushingRecipe(Ingredient.of(Blocks.GOLD_ORE),
						new ItemStack(Items.RAW_GOLD, 3)),
				null);
		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "raw_copper_from_copper_ore"),
				new CrushingRecipe(Ingredient.of(Blocks.COPPER_ORE),
						new ItemStack(Items.RAW_COPPER, 8)),
				null);

		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "redstone_dust_from_redstone_ore"),
				new CrushingRecipe(Ingredient.of(Blocks.REDSTONE_ORE),
						new ItemStack(Items.REDSTONE, 12)),
				null);
		recipeOutput.accept(
				new ResourceLocation(TestMod.MODID, "redstone_dust_from_redstone_block"),
				new CrushingRecipe(Ingredient.of(Blocks.REDSTONE_BLOCK),
						new ItemStack(Items.REDSTONE, 9)),
				null);
		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "lapis_dust_from_lapis_ore"),
				new CrushingRecipe(Ingredient.of(Blocks.LAPIS_ORE),
						new ItemStack(Items.LAPIS_LAZULI, 12)),
				null);
		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "lapis_dust_from_lapis_block"),
				new CrushingRecipe(Ingredient.of(Blocks.LAPIS_BLOCK),
						new ItemStack(Items.LAPIS_LAZULI, 9)),
				null);
		recipeOutput.accept(new ResourceLocation(TestMod.MODID, "diamond_from_diamond_ore"),
				new CrushingRecipe(Ingredient.of(Blocks.DIAMOND_ORE),
						new ItemStack(Items.DIAMOND, 2)),
				null);
	}
}
