package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.recipe.CrusherRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModRecipes {

    private TestModRecipes() {}

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TestMod.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CRUSHER_RECIPE_SERIALIZER =
            RECIPE_SERIALIZERS.register("crusher", () -> CrusherRecipe.Serializer.INSTANCE);

    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
