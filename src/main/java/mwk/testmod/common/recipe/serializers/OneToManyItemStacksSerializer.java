package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import mwk.testmod.common.recipe.base.crafter.OneToManyItemStackRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class OneToManyItemStacksSerializer<T extends OneToManyItemStackRecipe>
        implements RecipeSerializer<T> {

    public static final Codec<List<ItemStack>> LIST_OF_ITEM_STACKS_CODEC =
            Codec.list(ItemStack.CODEC);

    private final RecipeFactory<T> factory;
    private MapCodec<T> codec;
    private StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public OneToManyItemStacksSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public MapCodec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Ingredient.CODEC.fieldOf("input")
                                    .forGetter(OneToManyItemStackRecipe::getInputItem),
                            LIST_OF_ITEM_STACKS_CODEC.fieldOf("outputs")
                                    .forGetter(OneToManyItemStackRecipe::getOutputs))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        if (streamCodec == null) {
            streamCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, OneToManyItemStackRecipe::getInputItem,
                    ItemStack.LIST_STREAM_CODEC, OneToManyItemStackRecipe::getOutputs,
                    factory::create);
        }
        return streamCodec;
    }

    @FunctionalInterface
    public interface RecipeFactory<T extends OneToManyItemStackRecipe> {

        T create(Ingredient input, List<ItemStack> outputs);
    }
}
