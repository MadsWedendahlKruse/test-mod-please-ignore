package mwk.testmod.common.block.entity.base.crafter;

import mwk.testmod.common.block.entity.modules.ProcessingModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SingleCrafterBlockEntity<I extends RecipeInput, T extends Recipe<I>>
        extends CrafterBlockEntity<I, T> {

    protected SingleCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            RecipeType<T> recipeType, int maxProgress, int energyPerTick) {
        super(type, pos, state, recipeType, maxProgress, energyPerTick);
    }

    @Override
    public final void tick() {
        if (processing().isEmpty()) {
            return;
        }
        ProcessingModule<I, T> processing = processing().get();
        if (!processing.hasResource()) {
            setWorking(false);
            return;
        }
        T recipe = processing.getCurrentRecipe();
        if (canProcessRecipe(recipe)) {
            processing.increaseProgress();
            processing.consumeResource();
            setWorking(true);
            setChanged();
            if (processing.hasProgressFinished()) {
                processRecipe(recipe);
                processing.resetProgress();
            }
            if (sound().isPresent()) {
                sound().get().playSound();
            }
        } else {
            processing.resetProgress();
            setWorking(false);
        }
    }
}
