package mwk.testmod.common.block.entity.base.generator;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.common.recipe.base.generator.GeneratorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class GeneratorBlockEntity<T extends GeneratorRecipe>
        extends ProcessingBlockEntity<T> {

    public static final String NBT_TAG_MAX_PROGRESS = "maxProgress";

    private int energyGeneratedPerTick;

    protected GeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyGeneratedPerTick, int inputSlots, int outputSlots,
            int upgradeSlots, int[] inputTankCapacities, int[] outputTankCapacities,
            RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        // The generator can output twice as much energy as it can generate
        super(type, pos, state, maxEnergy, 2 * energyGeneratedPerTick, EnergyType.PRODUCER,
                inputSlots, outputSlots, upgradeSlots, inputTankCapacities, outputTankCapacities,
                Integer.MAX_VALUE, recipeType, sound, soundDuration);
        this.energyGeneratedPerTick = energyGeneratedPerTick;
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
                maxProgress = recipe.getEnergy() / energyGeneratedPerTick;
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

    protected boolean canGenerateEnergy() {
        return getEnergyStored() + energyGeneratedPerTick < getMaxEnergyStored();
    }

    protected void generateEnergy() {
        energyStorage.receiveEnergy(energyGeneratedPerTick, false);
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
            int maxTransfer = Math.min(getEnergyStored(), energyPerTick);
            int received = receiver.receiveEnergy(maxTransfer, false);
            energyStorage.extractEnergy(received, false);
        }
    }

    public int getEnergyGeneratedPerTick() {
        return energyGeneratedPerTick;
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
