package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.crafter.CatalystItemStackRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CatalystItemStackRecipeSerializer<T extends CatalystItemStackRecipe> implements
        RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private MapCodec<T> codec;
    private StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CatalystItemStackRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public MapCodec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Ingredient.CODEC.fieldOf("catalyst")
                                    .forGetter(CatalystItemStackRecipe::getCatalystItem),
                            Ingredient.CODEC.fieldOf("input")
                                    .forGetter(CatalystItemStackRecipe::getInputItem),
                            ItemStack.CODEC.fieldOf("output")
                                    .forGetter(CatalystItemStackRecipe::getOutputItem))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        if (streamCodec == null) {
            streamCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, CatalystItemStackRecipe::getCatalystItem,
                    Ingredient.CONTENTS_STREAM_CODEC, CatalystItemStackRecipe::getInputItem,
                    ItemStack.STREAM_CODEC, CatalystItemStackRecipe::getOutputItem,
                    factory::create);
        }
        return streamCodec;
    }

    @FunctionalInterface
    public interface RecipeFactory<T extends CatalystItemStackRecipe> {

        T create(Ingredient catalyst, Ingredient input, ItemStack output);
    }
}
