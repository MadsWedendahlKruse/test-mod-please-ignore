package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.OneToOneItemStackRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class OneToOneItemStackSerializer<T extends OneToOneItemStackRecipe>
        implements RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private Codec<T> codec;

    public OneToOneItemStackSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Codec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.create(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("input").forGetter(OneToOneItemStackRecipe::getInput),
                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output")
                            .forGetter(OneToOneItemStackRecipe::getResultItem))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf pBuffer) {
        Ingredient input = Ingredient.fromNetwork(pBuffer);
        ItemStack output = pBuffer.readItem();
        return factory.create(input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, OneToOneItemStackRecipe recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }
        buffer.writeItem(recipe.getResultItem());
    }

    public interface RecipeFactory<T extends OneToOneItemStackRecipe> {
        T create(Ingredient input, ItemStack output);
    }
}
