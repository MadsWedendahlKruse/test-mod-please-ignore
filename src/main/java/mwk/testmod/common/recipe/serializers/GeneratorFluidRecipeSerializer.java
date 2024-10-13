package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.generator.GeneratorFluidRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class GeneratorFluidRecipeSerializer<T extends GeneratorFluidRecipe>
        implements RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private MapCodec<T> codec;
    private StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public GeneratorFluidRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public MapCodec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.mapCodec(instance -> instance
                    .group(FluidStack.CODEC.fieldOf("input")
                                    .forGetter(GeneratorFluidRecipe::getInput),
                            Codec.INT.fieldOf("energy").forGetter(GeneratorFluidRecipe::getEnergy))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        if (streamCodec == null) {
            streamCodec = StreamCodec.composite(
                    FluidStack.STREAM_CODEC, GeneratorFluidRecipe::getInput,
                    ByteBufCodecs.INT, GeneratorFluidRecipe::getEnergy,
                    factory::create);
        }
        return streamCodec;
    }

    @FunctionalInterface
    public interface RecipeFactory<T extends GeneratorFluidRecipe> {

        T create(FluidStack input, int energy);
    }

}
