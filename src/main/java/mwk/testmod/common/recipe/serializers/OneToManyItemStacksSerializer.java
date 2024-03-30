package mwk.testmod.common.recipe.serializers;

import java.util.ArrayList;
import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.common.recipe.base.OneToManyItemStackRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class OneToManyItemStacksSerializer<T extends OneToManyItemStackRecipe>
        implements RecipeSerializer<T> {

    public static final Codec<List<ItemStack>> LIST_OF_ITEM_STACKS_CODEC =
            Codec.list(ItemStack.ITEM_WITH_COUNT_CODEC);

    private final RecipeFactory<T> factory;
    private Codec<T> codec;

    public OneToManyItemStacksSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public Codec<T> codec() {
        if (codec == null) {
            codec = RecordCodecBuilder.create(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("input").forGetter(OneToManyItemStackRecipe::getInput),
                    LIST_OF_ITEM_STACKS_CODEC.fieldOf("outputs")
                            .forGetter(OneToManyItemStackRecipe::getOutputs))
                    .apply(instance, factory::create));
        }
        return codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
        Ingredient input = Ingredient.fromNetwork(buffer);
        int outputCount = buffer.readByte();
        List<ItemStack> outputs = new ArrayList<ItemStack>();
        for (int i = 0; i < outputCount; i++) {
            outputs.add(buffer.readItem());
        }
        return factory.create(input, outputs);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }
        buffer.writeByte(recipe.getOutputs().size());
        for (ItemStack output : recipe.getOutputs()) {
            buffer.writeItem(output);
        }
    }

    public interface RecipeFactory<T extends OneToManyItemStackRecipe> {
        T create(Ingredient input, List<ItemStack> outputs);
    }
}
