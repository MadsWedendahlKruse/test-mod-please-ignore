package mwk.testmod.common.block.entity.base.crafter;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SingleCrafterBlockEntity<I extends RecipeInput, T extends Recipe<I>>
        extends CrafterBlockEntity<I, T> {

    protected SingleCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, int inputSlots, int outputSlots, int upgradeSlots,
            int[] inputTankCapacities, int[] outputTankCapacities, int maxProgress,
            RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, energyPerTick, inputSlots, outputSlots, upgradeSlots,
                inputTankCapacities, outputTankCapacities, maxProgress, recipeType, sound,
                soundDuration);
    }

    @Override
    public final void tick() {
        if (!hasEnergy()) {
            setWorking(false);
            return;
        }
        T recipe = getCurrentRecipe();
        if (canProcessRecipe(recipe)) {
            increaseProgress();
            consumeEnergy();
            setWorking(true);
            setChanged();
            if (hasProgressFinished()) {
                processRecipe(recipe);
                resetProgress();
            }
            playSound();
        } else {
            resetProgress();
            setWorking(false);
        }
    }
}
