package mwk.testmod.common.block.entity.base.crafter;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.item.upgrades.SpeedUpgradeItem;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CrafterBlockEntity<I extends RecipeInput, T extends Recipe<I>>
        extends ProcessingBlockEntity<I, T> {

    protected CrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, int inputSlots, int outputSlots, int upgradeSlots,
            int[] inputTankCapacities, int[] outputTankCapacities, int maxProgress,
            RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, energyPerTick, EnergyType.CONSUMER, inputSlots,
                outputSlots, upgradeSlots, inputTankCapacities, outputTankCapacities, maxProgress,
                recipeType, sound, soundDuration);
    }

    @Override
    protected void resetUpgrades() {
        maxProgress = maxProgressBase;
        progressPerTick = 1.0F;
        energyPerTick = energyPerTickBase;
    }

    @Override
    protected void installUpgrade(UpgradeItem upgrade) {
        if (upgrade instanceof SpeedUpgradeItem speedUpgrade) {
            progressPerTick += speedUpgrade.getSpeedMultiplier();
            maxProgress = (int) (maxProgressBase / progressPerTick);
            energyPerTick += energyPerTickBase * speedUpgrade.getEnergyMultiplier();
        }
    }
}
