package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.generator.GeneratorItemRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GeneratorItemRecipeSerializer<T extends GeneratorItemRecipe>
        implements RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private MapCodec<T> codec;
    private StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public GeneratorItemRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public MapCodec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.mapCodec(instance -> instance
                    .group(Ingredient.CODEC.fieldOf("input")
                                    .forGetter(GeneratorItemRecipe::getInputItem),
                            Codec.INT.fieldOf("energy").forGetter(GeneratorItemRecipe::getEnergy))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        if (streamCodec == null) {
            streamCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, GeneratorItemRecipe::getInputItem,
                    ByteBufCodecs.INT, GeneratorItemRecipe::getEnergy,
                    factory::create);
        }
        return streamCodec;
    }

    @FunctionalInterface
    public interface RecipeFactory<T extends GeneratorItemRecipe> {

        T create(Ingredient input, int energy);
    }
}
