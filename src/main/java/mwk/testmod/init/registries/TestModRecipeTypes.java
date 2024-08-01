package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.recipe.CrushingRecipe;
import mwk.testmod.common.recipe.GeothermalGeneratorRecipe;
import mwk.testmod.common.recipe.RedstoneGeneratorRecipe;
import mwk.testmod.common.recipe.SeparationRecipe;
import mwk.testmod.common.recipe.StirlingGeneratorRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModRecipeTypes {

    private TestModRecipeTypes() {}

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, TestMod.MODID);

    // Crafter Recipes
    public static final DeferredHolder<RecipeType<?>, RecipeType<CrushingRecipe>> CRUSHING =
            RECIPE_TYPES.register("crushing", () -> new RecipeType<CrushingRecipe>() {});
    public static final DeferredHolder<RecipeType<?>, RecipeType<SeparationRecipe>> SEPARATION =
            RECIPE_TYPES.register("separation", () -> new RecipeType<SeparationRecipe>() {});

    // Generator Recipes
    public static final DeferredHolder<RecipeType<?>, RecipeType<RedstoneGeneratorRecipe>> REDSTONE_GENERATOR =
            RECIPE_TYPES.register("redstone_generator",
                    () -> new RecipeType<RedstoneGeneratorRecipe>() {});
    public static final DeferredHolder<RecipeType<?>, RecipeType<GeothermalGeneratorRecipe>> GEOTHERMAL_GENERATOR =
            RECIPE_TYPES.register("geothermal_generator",
                    () -> new RecipeType<GeothermalGeneratorRecipe>() {});
    public static final DeferredHolder<RecipeType<?>, RecipeType<StirlingGeneratorRecipe>> STIRLING_GENERATOR =
            RECIPE_TYPES.register("stirling_generator",
                    () -> new RecipeType<StirlingGeneratorRecipe>() {});

    public static void register(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
    }
}
