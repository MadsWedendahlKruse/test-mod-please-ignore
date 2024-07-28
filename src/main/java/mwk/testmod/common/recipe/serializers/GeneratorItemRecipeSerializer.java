package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.generator.GeneratorItemRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GeneratorItemRecipeSerializer<T extends GeneratorItemRecipe>
        implements RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private Codec<T> codec;

    public GeneratorItemRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Codec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.create(instance -> instance
                    .group(Ingredient.CODEC.fieldOf("input")
                            .forGetter(GeneratorItemRecipe::getInput),
                            Codec.INT.fieldOf("energy").forGetter(GeneratorItemRecipe::getEnergy))
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

    @FunctionalInterface
    public interface RecipeFactory<T extends GeneratorItemRecipe> {
        T create(Ingredient input, int energy);
    }
}
