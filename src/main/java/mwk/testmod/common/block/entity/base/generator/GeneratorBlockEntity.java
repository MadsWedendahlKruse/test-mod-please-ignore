package mwk.testmod.common.block.entity.base.generator;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.block.entity.modules.ProcessingModule;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.common.recipe.base.generator.GeneratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GeneratorBlockEntity<I extends RecipeInput, T extends Recipe<I>>
        extends ProcessingBlockEntity<I, T> {

    public static final String NBT_TAG_MAX_PROGRESS = "maxProgress";

    protected GeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            RecipeType<T> recipeType, int energyGeneratedPerTick) {
        super(type, pos, state, recipeType, 0, energyGeneratedPerTick);
    }

    @Override
    public final void tick() {
        if (!canGenerateResource()) {
            setWorking(false);
            return;
        }
        if (processing().isEmpty()) {
            return;
        }
        ProcessingModule<I, T> processing = processing().get();
        if (processing.hasProgressFinished()) {
            processing.resetProgress();
            processing.setMaxProgress(Integer.MAX_VALUE);
        }
        if (processing.getProgress() == 0) {
            T recipe = processing.getCurrentRecipe();
            if (canProcessRecipe(recipe)) {
                setWorking(true);
                // TODO: What if they're not multiples of each other?
                processing.setMaxProgress(((GeneratorRecipe) recipe).getGeneratedAmount()
                        / processing.getResourcePerTick());
                processRecipe(recipe);
            } else {
                setWorking(false);
                return;
            }
        }
        processing.increaseProgress();
        generateResource();
        if (!isWorking()) {
            setWorking(true);
        }
        setChanged();
        if (sound().isPresent()) {
            sound().get().playSound();
        }
    }

//    @Override
//    protected boolean canProcessRecipe(T recipe) {
//        // This should never fail, but just in case
//        return recipe instanceof GeneratorRecipe && processing().map(
//                processingModule -> processingModule.canProcessRecipe(recipe)).orElse(false);
//    }

    @Override
    protected boolean hasResource() {
        return true;
    }

    @Override
    protected void consumeResource() {
        // Do nothing
    }

    private int getResourcePerTick() {
        return processing().map(ProcessingModule::getResourcePerTick).orElse(0);
    }

    protected boolean canGenerateResource() {
        // TODO: Instead of guessing, should we specify the resource type?
        if (energy().isPresent()) {
            return energy().get().getEnergyStored() + getResourcePerTick()
                    < energy().get().getMaxEnergyStored();
        }
        if (temporalFlux().isPresent()) {
            return temporalFlux().get().getTemporalFluxStored() + getResourcePerTick()
                    < temporalFlux().get().getMaxTemporalFluxStored();
        }
        return false;
    }

    protected void generateResource() {
        // TODO: Instead of guessing, should we specify the resource type?
        if (temporalFlux().isPresent()) {
            temporalFlux().get().recieveTemporalFlux(getResourcePerTick(), false);
        }
        if (energy().isPresent()) {
            energy().get().receiveEnergy(getResourcePerTick(), false);
        }
    }

    @Override
    public void installUpgrade(UpgradeItem upgrade) {
        // TODO: This should be handled differently for generators
        // if (upgrade instanceof SpeedUpgradeItem speedUpgrade) {
        // progressPerTick += speedUpgrade.getSpeedMultiplier();
        // maxProgress = (int) (maxProgressBase / progressPerTick);
        // energyPerTick += energyPerTickBase * speedUpgrade.getEnergyMultiplier();
        // }
    }

    @Override
    public void resetUpgrades() {
        // TODO: This should be handled differently for generators
        // maxProgress = maxProgressBase;
        // progressPerTick = 1.0F;
        // energyPerTick = energyPerTickBase;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        if (processing().isPresent()) {
            tag.putInt(NBT_TAG_MAX_PROGRESS, processing().get().getMaxProgress());
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(NBT_TAG_MAX_PROGRESS) && processing().isPresent()) {
            processing().get().setMaxProgress(tag.getInt(NBT_TAG_MAX_PROGRESS));
        }
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (processing().isPresent()) {
            tag.putInt(NBT_TAG_MAX_PROGRESS, processing().get().getMaxProgress());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, Provider registries) {
        super.handleUpdateTag(tag, registries);
        if (tag.contains(NBT_TAG_MAX_PROGRESS) && processing().isPresent()) {
            processing().get().setMaxProgress(tag.getInt(NBT_TAG_MAX_PROGRESS));
        }
    }
}
