package mwk.testmod.compat.jei;

import mezz.jei.api.recipe.RecipeType;
import mwk.testmod.TestMod;
import mwk.testmod.common.recipe.CrushingRecipe;
import mwk.testmod.common.recipe.SeparationRecipe;
import net.minecraft.resources.ResourceLocation;

public class JEITestModRecipeTypes {

        public static final RecipeType<CrushingRecipe> CRUSHING = new RecipeType<>(
                        new ResourceLocation(TestMod.MODID, "crushing"), CrushingRecipe.class);
        public static final RecipeType<SeparationRecipe> SEPARATION = new RecipeType<>(
                        new ResourceLocation(TestMod.MODID, "separation"), SeparationRecipe.class);
}
