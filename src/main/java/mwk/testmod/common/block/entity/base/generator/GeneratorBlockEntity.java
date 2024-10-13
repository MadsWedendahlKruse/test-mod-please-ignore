package mwk.testmod.common.block.entity.base.generator;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.common.recipe.base.generator.GeneratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GeneratorBlockEntity<I extends RecipeInput, T extends Recipe<I>>
        extends ProcessingBlockEntity<I, T> {

    public static final String NBT_TAG_MAX_PROGRESS = "maxProgress";

    protected GeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyGeneratedPerTick, int inputSlots, int outputSlots,
            int upgradeSlots, int[] inputTankCapacities, int[] outputTankCapacities,
            RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, energyGeneratedPerTick, EnergyType.PRODUCER, inputSlots,
                outputSlots, upgradeSlots, inputTankCapacities, outputTankCapacities,
                Integer.MAX_VALUE, recipeType, sound, soundDuration);
    }

    @Override
    public final void tick() {
        if (!canGenerateEnergy()) {
            setWorking(false);
            return;
        }
        if (hasProgressFinished()) {
            resetProgress();
            maxProgress = Integer.MAX_VALUE;
        }
        if (progress == 0) {
            T recipe = getCurrentRecipe();
            if (canProcessRecipe(recipe)) {
                setWorking(true);
                // TODO: What if they're not multiples of each other?
                maxProgress = ((GeneratorRecipe) recipe).getEnergy() / energyPerTick;
                processRecipe(recipe);
            } else {
                setWorking(false);
                return;
            }
        }
        increaseProgress();
        generateEnergy();
        setChanged();
        playSound();
    }

    @Override
    protected boolean canProcessRecipe(T recipe) {
        // This should never fail, but just in case
        return recipe instanceof GeneratorRecipe && super.canProcessRecipe(recipe);
    }

    protected boolean canGenerateEnergy() {
        return getEnergyStored() + energyPerTick < getMaxEnergyStored();
    }

    protected void generateEnergy() {
        energyStorage.receiveEnergy(energyPerTick, false);
    }

    @Override
    protected void resetUpgrades() {
        // TODO: This should be handled differently for generators
        // maxProgress = maxProgressBase;
        // progressPerTick = 1.0F;
        // energyPerTick = energyPerTickBase;
    }

    @Override
    protected void installUpgrade(UpgradeItem upgrade) {
        // TODO: This should be handled differently for generators
        // if (upgrade instanceof SpeedUpgradeItem speedUpgrade) {
        // progressPerTick += speedUpgrade.getSpeedMultiplier();
        // maxProgress = (int) (maxProgressBase / progressPerTick);
        // energyPerTick += energyPerTickBase * speedUpgrade.getEnergyMultiplier();
        // }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(NBT_TAG_MAX_PROGRESS, maxProgress);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(NBT_TAG_MAX_PROGRESS)) {
            maxProgress = tag.getInt(NBT_TAG_MAX_PROGRESS);
        }
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(NBT_TAG_MAX_PROGRESS, maxProgress);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, Provider registries) {
        super.handleUpdateTag(tag, registries);
        if (tag.contains(NBT_TAG_MAX_PROGRESS)) {
            maxProgress = tag.getInt(NBT_TAG_MAX_PROGRESS);
        }
    }
}
