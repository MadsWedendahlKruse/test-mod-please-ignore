package mwk.testmod.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.TestMod;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CrusherRecipe implements Recipe<SimpleContainer> {

    private final Ingredient input;
    private final ItemStack output;
    // private final ResourceLocation id;

    public CrusherRecipe(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
        // this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CrusherRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "crusher";
    }

    public static class Serializer implements RecipeSerializer<CrusherRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(TestMod.MODID, "crusher");

        @Override
        public Codec<CrusherRecipe> codec() {
            return RecordCodecBuilder.create(instance -> instance
                    .group(Ingredient.CODEC.fieldOf("input").forGetter(CrusherRecipe::getInput),
                            ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output")
                                    .forGetter(CrusherRecipe::getResultItem))
                    .apply(instance, CrusherRecipe::new));
        }

        @Override
        public CrusherRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            ItemStack output = pBuffer.readItem();
            return new CrusherRecipe(input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CrusherRecipe recipe) {
            // buffer.writeInt(recipe.input.getItems().length);
            // buffer.writeInt(1);
            // TODO: I haven't overridden getIngredients() in CrusherRecipe, so by default it
            // returns an empty list. Does this then work at all?
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.getResultItem());
        }
    }
}
