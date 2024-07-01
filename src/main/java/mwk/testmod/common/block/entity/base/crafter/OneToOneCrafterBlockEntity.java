package mwk.testmod.common.block.entity.base.crafter;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class OneToOneCrafterBlockEntity<T extends Recipe<Container>>
        extends SingleCrafterBlockEntity<T> {

    private static final int INPUT_SLOT_INDEX = 0;
    private static final int OUTPUT_SLOT_INDEX = 1;

    protected OneToOneCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, int maxProgress, RecipeType<T> recipeType,
            SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, energyPerTick, 1, 1, 6, maxProgress, recipeType, sound,
                soundDuration);
    }

    @Override
    protected boolean canProcessRecipe(T recipe) {
        if (recipe == null) {
            return false;
        }
        ItemStack result = recipe.getResultItem(null);
        return canInsertItemIntoSlot(OUTPUT_SLOT_INDEX, result.getItem(), result.getCount());
    }

    @Override
    protected void processItem(T recipe) {
        ItemStack result = recipe.getResultItem(null);
        this.inventory.extractItem(INPUT_SLOT_INDEX, 1, false);
        this.inventory.setStackInSlot(OUTPUT_SLOT_INDEX, new ItemStack(result.getItem(),
                this.inventory.getStackInSlot(OUTPUT_SLOT_INDEX).getCount() + result.getCount()));
    }

}
