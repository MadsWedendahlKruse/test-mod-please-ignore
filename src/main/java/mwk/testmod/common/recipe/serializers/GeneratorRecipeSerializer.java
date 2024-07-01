package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.generator.GeneratorRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GeneratorRecipeSerializer<T extends GeneratorRecipe> implements RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private Codec<T> codec;

    public GeneratorRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Codec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.create(instance -> instance
                    .group(Ingredient.CODEC.fieldOf("input").forGetter(GeneratorRecipe::getInput),
                            Codec.INT.fieldOf("energy").forGetter(GeneratorRecipe::getEnergy))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
        Ingredient input = Ingredient.fromNetwork(buffer);
        int energy = buffer.readInt();
        return factory.create(input, energy);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        recipe.getInput().toNetwork(buffer);
        buffer.writeInt(recipe.getEnergy());
    }

    public interface RecipeFactory<T extends GeneratorRecipe> {
        T create(Ingredient input, int energy);
    }
}
