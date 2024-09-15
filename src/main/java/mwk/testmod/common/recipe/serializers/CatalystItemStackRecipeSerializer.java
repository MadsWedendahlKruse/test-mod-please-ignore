package mwk.testmod.common.recipe.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.crafter.CatalystItemStackRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CatalystItemStackRecipeSerializer<T extends CatalystItemStackRecipe>
        implements RecipeSerializer<T> {

    private final RecipeFactory<T> factory;
    private Codec<T> codec;

    public CatalystItemStackRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Codec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.create(instance -> instance.group(
                            Ingredient.CODEC.fieldOf("catalyst")
                                    .forGetter(CatalystItemStackRecipe::getCatalyst),
                            Ingredient.CODEC.fieldOf("input").forGetter(CatalystItemStackRecipe::getInput),
                            ItemStack.CODEC.fieldOf("output").forGetter(CatalystItemStackRecipe::getOutput))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf pBuffer) {
        Ingredient catalyst = Ingredient.fromNetwork(pBuffer);
        Ingredient input = Ingredient.fromNetwork(pBuffer);
        ItemStack output = pBuffer.readItem();
        return factory.create(catalyst, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pRecipe.getCatalyst().toNetwork(pBuffer);
        pRecipe.getInput().toNetwork(pBuffer);
        pBuffer.writeItem(pRecipe.getOutput());
    }

    public interface RecipeFactory<T extends CatalystItemStackRecipe> {

        T create(Ingredient catalyst, Ingredient input, ItemStack output);
    }
}
