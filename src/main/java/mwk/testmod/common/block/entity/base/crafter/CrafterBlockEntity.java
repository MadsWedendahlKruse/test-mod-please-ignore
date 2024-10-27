package mwk.testmod.common.block.entity.base.crafter;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.block.entity.modules.ProcessingModule;
import mwk.testmod.common.item.upgrades.SpeedUpgradeItem;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CrafterBlockEntity<I extends RecipeInput, T extends Recipe<I>>
        extends ProcessingBlockEntity<I, T> {

    protected CrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            RecipeType<T> recipeType, int maxProgress, int energyPerTick) {
        super(type, pos, state, recipeType, maxProgress, energyPerTick);
    }

    @Override
    public void resetUpgrades() {
        if (processing().isPresent()) {
            ProcessingModule<I, T> processing = processing().get();
            processing.setMaxProgress(processing.maxProgressBase);
            processing.setProgressPerTick(1.0F);
            processing.setResourcePerTick(processing.resourcePerTickBase);
        }
    }

    @Override
    public void installUpgrade(UpgradeItem upgrade) {
        if (upgrade instanceof SpeedUpgradeItem speedUpgrade && processing().isPresent()) {
            ProcessingModule<I, T> processing = processing().get();
            float progressPerTick = processing.getProgressPerTick();
            progressPerTick += speedUpgrade.getSpeedMultiplier();
            processing.setProgressPerTick(progressPerTick);
            processing.setMaxProgress((int) (processing.maxProgressBase / progressPerTick));
            int resourcePerTick = processing.getResourcePerTick();
            processing.setResourcePerTick((int) (resourcePerTick
                    + processing.resourcePerTickBase * speedUpgrade.getEnergyMultiplier()));
        }
    }
}
