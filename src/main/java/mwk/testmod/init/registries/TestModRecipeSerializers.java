package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.recipe.CrushingRecipe;
import mwk.testmod.common.recipe.SeparationRecipe;
import mwk.testmod.common.recipe.serializers.OneToManyItemStacksSerializer;
import mwk.testmod.common.recipe.serializers.OneToOneItemStackSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModRecipeSerializers {

	private TestModRecipeSerializers() {}

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
			DeferredRegister.create(Registries.RECIPE_SERIALIZER, TestMod.MODID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CRUSHING_RECIPE_SERIALIZER =
			RECIPE_SERIALIZERS.register("crushing",
					() -> new OneToOneItemStackSerializer<>(CrushingRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SEPARATION_RECIPE_SERIALIZER =
			RECIPE_SERIALIZERS.register("separation",
					() -> new OneToManyItemStacksSerializer<>(SeparationRecipe::new));

	public static void register(IEventBus modEventBus) {
		RECIPE_SERIALIZERS.register(modEventBus);
	}
}