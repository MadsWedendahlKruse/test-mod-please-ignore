package mwk.testmod.common.block.entity;

import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.entity.modules.AutoIOModule;
import mwk.testmod.common.block.entity.modules.InventoryModule;
import mwk.testmod.common.block.entity.modules.TemporalFluxModule;
import mwk.testmod.common.block.inventory.TemporalSieveMenu;
import mwk.testmod.common.recipe.TemporalSieveRecipe;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TemporalSieveBlockEntity extends
        GeneratorBlockEntity<SingleRecipeInput, TemporalSieveRecipe> {

    public TemporalSieveBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.TEMPORAL_SIEVE_ENTITY_TYPE.get(), pos, state,
                TestModRecipeTypes.TEMPORAL_SIEVE.get(), 20);
        addModule(new InventoryModule(this, 1, 0, 6, this::onInventoryChanged,
                this::isInputItemValid));
        addModule(new TemporalFluxModule(this, 50000));
        addModule(new AutoIOModule());
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.TEMPORAL_SIEVE.get().getName();
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_TEMPORAL_SIEVE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            Player player) {
        return new TemporalSieveMenu(containerId, player, worldPosition);
    }

    @Override
    protected SingleRecipeInput getRecipeInput() {
        ItemStack stack = inventory().map(inventory -> inventory.getStackInSlot(0))
                .orElse(ItemStack.EMPTY);
        return new SingleRecipeInput(stack);
    }

    @Override
    protected boolean isSameInput(SingleRecipeInput input1, SingleRecipeInput input2) {
        return ItemStack.matches(input1.getItem(0), input2.getItem(0));
    }

    @Override
    protected boolean canProcessRecipe(TemporalSieveRecipe recipe) {
        if (recipe == null) {
            return false;
        }
        if (inventory().isPresent()) {
            InventoryModule inventory = inventory().get();
            ItemStack result = recipe.getResultItem(null);
            return result.isEmpty() || inventory.canInsertItemIntoSlot(0, result.getItem(),
                    result.getCount());
        }
        return false;
    }

    @Override
    protected void processRecipe(TemporalSieveRecipe recipe) {
        if (inventory().isPresent()) {
            InventoryModule inventory = inventory().get();
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (int i = 0; i < ingredients.size(); i++) {
                // TODO: Ingredients can have multiple items?
                ItemStack ingredient = ingredients.get(i).getItems()[0];
                inventory.extractItem(i, ingredient.getCount(), false);
            }
            ItemStack result = recipe.getResultItem(null);
            if (!result.isEmpty()) {
                inventory.setStackInSlot(0, new ItemStack(result.getItem(),
                        inventory.getStackInSlot(0).getCount() + result.getCount()));
            }
        }
    }
}
