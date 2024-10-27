package mwk.testmod.common.block.entity.base.crafter;

import java.util.List;
import mwk.testmod.common.block.entity.modules.InventoryModule;
import mwk.testmod.common.recipe.base.crafter.OneToManyItemStackRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class OneToManyCrafterBlockEntity<T extends OneToManyItemStackRecipe>
        extends SingleCrafterBlockEntity<SingleRecipeInput, T> {

    protected OneToManyCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            RecipeType<T> recipeType, int maxProgress, int energyPerTick) {
        super(type, pos, state, recipeType, maxProgress, energyPerTick);
    }

    @Override
    protected boolean canProcessRecipe(T recipe) {
        if (recipe == null) {
            return false;
        }
        if (inventory().isEmpty()) {
            return false;
        }
        InventoryModule inventory = inventory().get();
        List<ItemStack> results = recipe.getOutputs();
        for (int i = 0; i < results.size(); i++) {
            ItemStack result = results.get(i);
            // Index 0 is the input slot, so we start at index 1
            if (!inventory.canInsertItemIntoSlot(i + 1, result.getItem(), result.getCount())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void processRecipe(T recipe) {
        if (inventory().isPresent()) {
            InventoryModule inventory = inventory().get();
            List<ItemStack> results = recipe.getOutputs();
            // Index 0 is the input slot
            inventory.extractItem(0, 1, false);
            for (int i = 0; i < results.size(); i++) {
                ItemStack result = results.get(i);
                int outputSlot = i + inventory.getInputSlots();
                inventory.setStackInSlot(outputSlot, new ItemStack(result.getItem(),
                        inventory.getStackInSlot(outputSlot).getCount() + result.getCount()));
            }
        }
    }

    @Override
    protected SingleRecipeInput getRecipeInput() {
        return inventory().map(inventory -> new SingleRecipeInput(inventory.getStackInSlot(0)))
                .orElse(new SingleRecipeInput(ItemStack.EMPTY));
    }

    @Override
    protected boolean isSameInput(SingleRecipeInput input1, SingleRecipeInput input2) {
        return ItemStack.matches(input1.getItem(0), input2.getItem(0));
    }
}
