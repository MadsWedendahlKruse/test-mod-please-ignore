package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.generator.GeneratorFluidRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class GeneratorFluidRecipeSerializer<T extends GeneratorFluidRecipe>
        implements RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private Codec<T> codec;

    public GeneratorFluidRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Codec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.create(instance -> instance
                    .group(FluidStack.CODEC.fieldOf("input")
                            .forGetter(GeneratorFluidRecipe::getInput),
                            Codec.INT.fieldOf("energy").forGetter(GeneratorFluidRecipe::getEnergy))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation fluidName = new ResourceLocation(buffer.readUtf());
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidName);
        FluidStack input = new FluidStack(fluid, buffer.readInt());
        int energy = buffer.readInt();
        return factory.create(input, energy);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(BuiltInRegistries.FLUID.getKey(recipe.getInput().getFluid()).toString());
        buffer.writeInt(recipe.getInput().getAmount());
        buffer.writeInt(recipe.getEnergy());
    }

    @FunctionalInterface
    public interface RecipeFactory<T extends GeneratorFluidRecipe> {
        T create(FluidStack input, int energy);
    }

}
