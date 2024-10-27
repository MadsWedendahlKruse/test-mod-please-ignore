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
        if (!canGenerateEnergy()) {
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
                processing.setMaxProgress(((GeneratorRecipe) recipe).getEnergy()
                        / processing.getResourcePerTick());
                processRecipe(recipe);
            } else {
                setWorking(false);
                return;
            }
        }
        processing.increaseProgress();
        generateEnergy();
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

    private int getEnergyPerTick() {
        return processing().map(ProcessingModule::getResourcePerTick).orElse(0);
    }

    protected boolean canGenerateEnergy() {
        return energy().map(energyModule -> energyModule.getEnergyStored() + getEnergyPerTick()
                < energyModule.getMaxEnergyStored()).orElse(false);
    }

    protected void generateEnergy() {
        energy().ifPresent(energyModule -> energyModule.receiveEnergy(getEnergyPerTick(), false));
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
