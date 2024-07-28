package mwk.testmod.common.recipe;

import mwk.testmod.common.recipe.base.generator.GeneratorFluidRecipe;
import mwk.testmod.init.registries.TestModRecipeSerializers;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public class GeothermalGeneratorRecipe extends GeneratorFluidRecipe {

    public GeothermalGeneratorRecipe(FluidStack input, int energy) {
        super(input, energy);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TestModRecipeSerializers.GEOTHERMAL_GENERATOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TestModRecipeTypes.GEOTHERMAL_GENERATOR.get();
    }

}
