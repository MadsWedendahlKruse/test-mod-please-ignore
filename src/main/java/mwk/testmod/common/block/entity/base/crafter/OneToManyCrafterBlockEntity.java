package mwk.testmod.common.block.entity.base.crafter;

import java.util.List;
import mwk.testmod.common.recipe.SeparationRecipe;
import mwk.testmod.common.recipe.base.crafter.OneToManyItemStackRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class OneToManyCrafterBlockEntity<T extends OneToManyItemStackRecipe>
        extends SingleCrafterBlockEntity<T> {

    protected OneToManyCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, int inputSlots, int outputSlots, int upgradeSlots,
            int maxProgress, RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, energyPerTick, inputSlots, outputSlots, upgradeSlots,
                EMPTY_TANKS, EMPTY_TANKS, maxProgress, recipeType, sound, soundDuration);
    }

    @Override
    protected boolean canProcessRecipe(T recipe) {
        if (recipe == null) {
            return false;
        }
        List<ItemStack> results = recipe.getOutputs();
        for (int i = 0; i < results.size(); i++) {
            ItemStack result = results.get(i);
            // Index 0 is the input slot, so we start at index 1
            if (!canInsertItemIntoSlot(i + 1, result.getItem(), result.getCount())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void processRecipe(T recipe) {
        List<ItemStack> results = recipe.getOutputs();
        // Index 0 is the input slot
        this.inventory.extractItem(0, 1, false);
        for (int i = 0; i < results.size(); i++) {
            ItemStack result = results.get(i);
            int outputSlot = i + 1;
            this.inventory.setStackInSlot(outputSlot, new ItemStack(result.getItem(),
                    this.inventory.getStackInSlot(outputSlot).getCount() + result.getCount()));
        }
    }

}
