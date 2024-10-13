package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.crafter.OneToOneItemStackRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class OneToOneItemStackSerializer<T extends OneToOneItemStackRecipe>
        implements RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private MapCodec<T> codec;
    private StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public OneToOneItemStackSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public MapCodec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Ingredient.CODEC.fieldOf("input")
                                    .forGetter(OneToOneItemStackRecipe::getInputItem),
                            ItemStack.CODEC.fieldOf("output")
                                    .forGetter(OneToOneItemStackRecipe::getResultItem))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        if (streamCodec == null) {
            streamCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, OneToOneItemStackRecipe::getInputItem,
                    ItemStack.STREAM_CODEC, OneToOneItemStackRecipe::getResultItem,
                    factory::create);
        }
        return streamCodec;
    }

    @FunctionalInterface
    public interface RecipeFactory<T extends OneToOneItemStackRecipe> {

        T create(Ingredient input, ItemStack output);
    }
}
