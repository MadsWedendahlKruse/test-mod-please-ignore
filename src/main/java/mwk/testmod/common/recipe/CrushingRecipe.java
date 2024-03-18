package mwk.testmod.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mwk.testmod.TestMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CrushingRecipe implements Recipe<Container> {

    private final Ingredient input;
    private final ItemStack output;
    // private final ResourceLocation id;

    public CrushingRecipe(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
        // this.id = id;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
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

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(input);
        return ingredients;
    }

    public static class Type implements RecipeType<CrushingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "crushing";
    }

    public static class Serializer implements RecipeSerializer<CrushingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(TestMod.MODID, "crushing");

        @Override
        public Codec<CrushingRecipe> codec() {
            return RecordCodecBuilder.create(instance -> instance
                    .group(Ingredient.CODEC.fieldOf("input").forGetter(CrushingRecipe::getInput),
                            ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output")
                                    .forGetter(CrushingRecipe::getResultItem))
                    .apply(instance, CrushingRecipe::new));
        }

        @Override
        public CrushingRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            ItemStack output = pBuffer.readItem();
            return new CrushingRecipe(input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CrushingRecipe recipe) {
            // buffer.writeInt(recipe.input.getItems().length);
            // buffer.writeInt(1);
            // TODO: I haven't overridden getIngredients() in CrushingRecipe, so by default it
            // returns an empty list. Does this then work at all?
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.getResultItem());
        }
    }
}
