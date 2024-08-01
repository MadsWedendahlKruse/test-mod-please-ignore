package mwk.testmod.common.block.entity.base.generator;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.common.recipe.base.generator.GeneratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class GeneratorBlockEntity<T extends Recipe<Container>>
        extends ProcessingBlockEntity<T> {

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

    public void pushEnergy(BlockPos pos) {
        if (getEnergyStored() == 0) {
            return;
        }
        for (Direction direction : Direction.values()) {
            // TODO: Capability cache
            IEnergyStorage receiver = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            if (receiver == null || receiver == this.getEnergyStorage(direction)) {
                continue;
            }
            // We don't want to transfer more energy than we have
            // Generator can push twice as much energy as it can generate so we don't
            // end up with a full buffer that never gets emptied
            int maxTransfer = Math.min(getEnergyStored(), 2 * energyPerTick);
            int received = receiver.receiveEnergy(maxTransfer, false);
            energyStorage.extractEnergy(received, false);
        }
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
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(NBT_TAG_MAX_PROGRESS, maxProgress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NBT_TAG_MAX_PROGRESS)) {
            maxProgress = tag.getInt(NBT_TAG_MAX_PROGRESS);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt(NBT_TAG_MAX_PROGRESS, maxProgress);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains(NBT_TAG_MAX_PROGRESS)) {
            maxProgress = tag.getInt(NBT_TAG_MAX_PROGRESS);
        }
    }
}
