package mwk.testmod.common.block.entity.base.crafter;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.item.upgrades.SpeedUpgradeItem;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CrafterBlockEntity<T extends Recipe<Container>>
        extends ProcessingBlockEntity<T> {

    protected CrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, int inputSlots, int outputSlots, int upgradeSlots,
            int maxProgress, RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, energyPerTick, EnergyType.CONSUMER, inputSlots,
                outputSlots, upgradeSlots, maxProgress, recipeType, sound, soundDuration);
        // TODO Auto-generated constructor stub
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
