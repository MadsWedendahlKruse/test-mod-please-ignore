package mwk.testmod.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import mwk.testmod.TestMod;
import mwk.testmod.common.recipe.CrushingRecipe;
import mwk.testmod.common.recipe.GeothermalGeneratorRecipe;
import mwk.testmod.common.recipe.RedstoneGeneratorRecipe;
import mwk.testmod.common.recipe.SeparationRecipe;
import mwk.testmod.common.recipe.StampingRecipe;
import mwk.testmod.common.recipe.StirlingGeneratorRecipe;
import mwk.testmod.common.util.TestModTags;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class TestModRecipeProvider extends RecipeProvider {

    public TestModRecipeProvider(PackOutput packOutput,
            CompletableFuture<Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Crafting recipes
        registerShapelessRecipes(recipeOutput);
        registerShapedRecipes(recipeOutput);
        // Processing recipes
        registerCookingRecipes(recipeOutput);
        registerCrushingRecipes(recipeOutput);
        registerSeparationRecipes(recipeOutput);
        registerStampingRecipes(recipeOutput);
        // Generator recipes
        registerRedstoneGeneratorRecipes(recipeOutput);
        registerGeothermalGeneratorRecipes(recipeOutput);
        registerStirlingGeneratorRecipes(recipeOutput);
    }

    private void registerShapelessRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TestModItems.STEEL_DUST)
                .requires(TestModTags.Items.IRON_DUST).requires(TestModTags.Items.COAL_DUST)
                .unlockedBy(getHasName(TestModItems.IRON_DUST), has(TestModTags.Items.IRON_DUST))
                .save(recipeOutput);
    }

    private void registerShapedRecipes(RecipeOutput recipeOutput) {
        // Machine Frames
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TestModBlocks.MACHINE_FRAME_BASIC)
                .pattern("CIC")
                .pattern("I I")
                .pattern("CIC")
                .define('I', TestModItems.IRON_PLATE)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(TestModItems.IRON_PLATE), has(TestModItems.IRON_PLATE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TestModBlocks.MACHINE_FRAME_REINFORCED)
                .pattern("ISI")
                .pattern("SMS")
                .pattern("ISI")
                .define('S', TestModItems.STEEL_PLATE)
                .define('I', Items.IRON_INGOT)
                .define('M', TestModBlocks.MACHINE_FRAME_BASIC)
                .unlockedBy(getHasName(TestModItems.STEEL_PLATE), has(TestModItems.STEEL_PLATE))
                .save(recipeOutput);
        // Misc
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TestModItems.RED_GIZMO)
                .pattern("GGG")
                .pattern("RIR")
                .pattern(" C ")
                .define('G', Items.GOLD_NUGGET)
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TestModItems.BLUE_GIZMO)
                .pattern("GGG")
                .pattern("LIL")
                .pattern(" C ")
                .define('G', Items.GOLD_NUGGET)
                .define('L', Items.LAPIS_LAZULI)
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.LAPIS_LAZULI), has(Items.LAPIS_LAZULI))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TestModItems.ENERGY_CELL)
                .pattern("IGI")
                .pattern("IRI")
                .pattern("IRI")
                .define('I', Items.IRON_NUGGET)
                .define('G', TestModItems.RED_GIZMO)
                .define('R', Items.REDSTONE)
                .unlockedBy(getHasName(TestModItems.RED_GIZMO), has(TestModItems.RED_GIZMO))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TestModBlocks.ENERGY_CUBE)
                .pattern("EIE")
                .pattern("CMC")
                .pattern("EIE")
                .define('M', TestModBlocks.MACHINE_FRAME_BASIC)
                .define('E', TestModItems.ENERGY_CELL)
                .define('C', TestModItems.COPPER_PLATE)
                .define('I', Items.IRON_INGOT)
                .unlockedBy(getHasName(TestModItems.ENERGY_CELL), has(TestModItems.ENERGY_CELL))
                .save(recipeOutput);
    }

    private void registerCookingRecipes(RecipeOutput recipeOutput) {
        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(TestModTags.Items.STEEL_DUST), RecipeCategory.MISC,
                        TestModItems.STEEL_INGOT.get(), 1.0F, 200)
                .unlockedBy(getHasName(TestModItems.STEEL_DUST), has(TestModTags.Items.STEEL_DUST))
                .save(recipeOutput);
        SimpleCookingRecipeBuilder
                .blasting(Ingredient.of(TestModTags.Items.TITANIUM_DUST), RecipeCategory.MISC,
                        TestModItems.TITANIUM_INGOT.get(), 1.0F, 200)
                .unlockedBy(getHasName(TestModItems.TITANIUM_DUST),
                        has(TestModTags.Items.TITANIUM_DUST))
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
        registerCrushingRecipe(recipeOutput, "iron_dust_from_raw_iron",
                Ingredient.of(Items.RAW_IRON), TestModItems.IRON_DUST);
        registerCrushingRecipe(recipeOutput, "steel_dust_from_steel_ingot",
                Ingredient.of(TestModTags.Items.STEEL_INGOT), TestModItems.STEEL_DUST);
        registerCrushingRecipe(recipeOutput, "ilmenite_dust_from_raw_ilmenite",
                Ingredient.of(TestModTags.Items.RAW_ILMENITE), TestModItems.ILMENITE_DUST);
    }

    private void registerSeparationRecipe(RecipeOutput recipeOutput, String name, Ingredient input,
            List<ItemStack> outputs) {
        recipeOutput.accept(new ResourceLocation(TestMod.MODID, name),
                new SeparationRecipe(input, outputs), null);
    }

    private void registerSeparationRecipes(RecipeOutput recipeOutput) {
        registerSeparationRecipe(recipeOutput, "steel_dust_separation",
                Ingredient.of(TestModTags.Items.STEEL_DUST),
                new ArrayList<ItemStack>(Arrays.asList(new ItemStack(TestModItems.IRON_DUST.get()),
                        new ItemStack(TestModItems.COAL_DUST.get()))));
        registerSeparationRecipe(recipeOutput, "ilmenite_dust_separation",
                Ingredient.of(TestModTags.Items.ILMENITE_DUST),
                new ArrayList<ItemStack>(
                        Arrays.asList(new ItemStack(TestModItems.TITANIUM_DUST.get()),
                                new ItemStack(TestModItems.IRON_DUST.get()))));
    }

    private void registerStampingRecipe(RecipeOutput recipeOutput, String name,
            Ingredient stampingDie, Ingredient input, ItemStack output) {
        recipeOutput.accept(new ResourceLocation(TestMod.MODID, name),
                new StampingRecipe(stampingDie, input, output), null);
    }

    private void registerStampingRecipes(RecipeOutput recipeOutput) {
        // Plates
        registerStampingRecipe(recipeOutput, "iron_plate_stamping",
                Ingredient.of(TestModItems.PLATE_STAMPING_DIE), Ingredient.of(Items.IRON_INGOT),
                new ItemStack(TestModItems.IRON_PLATE.get()));
        registerStampingRecipe(recipeOutput, "copper_plate_stamping",
                Ingredient.of(TestModItems.PLATE_STAMPING_DIE), Ingredient.of(Items.COPPER_INGOT),
                new ItemStack(TestModItems.COPPER_PLATE.get()));
        registerStampingRecipe(recipeOutput, "steel_plate_stamping",
                Ingredient.of(TestModItems.PLATE_STAMPING_DIE),
                Ingredient.of(TestModTags.Items.STEEL_INGOT),
                new ItemStack(TestModItems.STEEL_PLATE.get()));
        // Gears
        registerStampingRecipe(recipeOutput, "iron_gear_stamping",
                Ingredient.of(TestModItems.GEAR_STAMPING_DIE), Ingredient.of(Items.IRON_INGOT),
                new ItemStack(TestModItems.IRON_GEAR.get()));
    }

    private void registerRedstoneGeneratorRecipe(RecipeOutput recipeOutput, String name,
            Ingredient input, int energy) {
        recipeOutput.accept(new ResourceLocation(TestMod.MODID, name),
                new RedstoneGeneratorRecipe(input, energy), null);
    }

    private static final int ENERGY_PER_REDSTONE = 16384;

    private void registerRedstoneGeneratorRecipes(RecipeOutput recipeOutput) {
        registerRedstoneGeneratorRecipe(recipeOutput, "energy_from_redstone_dust",
                Ingredient.of(Items.REDSTONE), ENERGY_PER_REDSTONE);
        registerRedstoneGeneratorRecipe(recipeOutput, "energy_from_redstone_block",
                Ingredient.of(Blocks.REDSTONE_BLOCK), ENERGY_PER_REDSTONE * 9);
    }

    private static final int ENERGY_PER_LAVA_BUCKET = 131072;

    private void registerGeothermalGeneratorRecipe(RecipeOutput recipeOutput, String name,
            FluidStack input, int energy) {
        recipeOutput.accept(new ResourceLocation(TestMod.MODID, name),
                new GeothermalGeneratorRecipe(input, energy), null);
    }

    private void registerGeothermalGeneratorRecipes(RecipeOutput recipeOutput) {
        registerGeothermalGeneratorRecipe(recipeOutput, "energy_from_lava_bucket",
                new FluidStack(Fluids.LAVA, 1000), ENERGY_PER_LAVA_BUCKET);
    }

    private static final int ENERGY_PER_BURN_TIME = 10;

    private void registerStirlingGeneratorRecipe(RecipeOutput recipeOutput, String name,
            Ingredient input, int energy) {
        recipeOutput.accept(new ResourceLocation(TestMod.MODID, name),
                new StirlingGeneratorRecipe(input, energy), null);
    }

    private void registerStirlingGeneratorRecipe(RecipeOutput recipeOutput, ItemLike item,
            int burnTime) {
        String itemName = item.asItem().getDescriptionId().replace('.', '_');
        registerStirlingGeneratorRecipe(recipeOutput, "energy_from_" + itemName,
                Ingredient.of(item), burnTime * ENERGY_PER_BURN_TIME);
    }

    private void registerStirlingGeneratorRecipe(RecipeOutput recipeOutput, TagKey<Item> tag,
            int burnTime) {
        String tagString = tag.location().toString().replace(':', '_').replace('/', '_');
        registerStirlingGeneratorRecipe(recipeOutput, "energy_from_" + tagString,
                Ingredient.of(tag), burnTime * ENERGY_PER_BURN_TIME);
    }

    private void registerStirlingGeneratorRecipes(RecipeOutput recipeOutput) {
        // First solutions was the snippet below, but for some reason it skipped the
        // tags

        // TODO: Not sure if I vibe with this solution
        // Map<Item, Integer> fuelBurnTime = AbstractFurnaceBlockEntity.getFuel();
        // for (Map.Entry<Item, Integer> entry : fuelBurnTime.entrySet()) {
        // Item item = entry.getKey();
        // // Lava buckets are handled by the geothermal generator
        // // TODO: This is a bit of a hack, but it works for now
        // if (item == Items.LAVA_BUCKET) {
        // continue;
        // }
        // registerStirlingGeneratorRecipe(recipeOutput, "energy_from_" + item.getDescriptionId(),
        // Ingredient.of(item), entry.getValue() * ENERGY_PER_BURN_TIME);
        // }

        // Second solution is copied directly from AbstracyFurnaceBlockEntity
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.COAL_BLOCK, 16000);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.BLAZE_ROD, 2400);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.COAL, 1600);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.CHARCOAL, 1600);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.LOGS, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.BAMBOO_BLOCKS, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.PLANKS, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.BAMBOO_MOSAIC, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOODEN_STAIRS, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.BAMBOO_MOSAIC_STAIRS, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOODEN_SLABS, 150);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.BAMBOO_MOSAIC_SLAB, 150);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOODEN_TRAPDOORS, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOODEN_PRESSURE_PLATES,
                300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOODEN_FENCES, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.FENCE_GATES, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.NOTE_BLOCK, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.BOOKSHELF, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.CHISELED_BOOKSHELF, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.LECTERN, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.JUKEBOX, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.CHEST, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.TRAPPED_CHEST, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.CRAFTING_TABLE, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.DAYLIGHT_DETECTOR, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.BANNERS, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.BOW, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.FISHING_ROD, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.LADDER, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.SIGNS, 200);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.HANGING_SIGNS, 800);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.WOODEN_SHOVEL, 200);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.WOODEN_SWORD, 200);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.WOODEN_HOE, 200);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.WOODEN_AXE, 200);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.WOODEN_PICKAXE, 200);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOODEN_DOORS, 200);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.BOATS, 1200);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOOL, 100);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOODEN_BUTTONS, 100);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.STICK, 100);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.SAPLINGS, 100);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.BOWL, 100);
        registerStirlingGeneratorRecipe(recipeOutput, (TagKey) ItemTags.WOOL_CARPETS, 67);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.DRIED_KELP_BLOCK, 4001);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Items.CROSSBOW, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.BAMBOO, 50);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.DEAD_BUSH, 100);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.SCAFFOLDING, 50);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.LOOM, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.BARREL, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.CARTOGRAPHY_TABLE, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.FLETCHING_TABLE, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.SMITHING_TABLE, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.COMPOSTER, 300);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.AZALEA, 100);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.FLOWERING_AZALEA, 100);
        registerStirlingGeneratorRecipe(recipeOutput, (ItemLike) Blocks.MANGROVE_ROOTS, 300);
    }

}
