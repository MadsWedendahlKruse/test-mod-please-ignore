package mwk.testmod.common.block.entity.base;

import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.item.upgrades.SpeedUpgradeItem;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * A block entity that can craft items using recipes.
 */
public abstract class CrafterMachineBlockEntity<T extends Recipe<Container>>
        extends BaseMachineBlockEntity implements ITickable {

    public static final String NBT_TAG_PROGRESS = "progress";

    protected final RecipeType<T> recipeType;

    private int progress;
    private int maxProgress;
    // Storing the progress per tick as a float makes applying upgrades easier
    private float progressPerTick;
    private int energyPerTick;
    // Base values before upgrades
    public final int maxProgressBase;
    public final int energyPerTickBase;

    private final SoundEvent sound;
    private final int soundDuration; // in ticks
    private long soundStart; // in ticks

    protected CrafterMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, int inputSlots, int outputSlots, int upgradeSlots,
            int maxProgress, RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, EnergyType.CONSUMER, inputSlots, outputSlots,
                upgradeSlots);
        this.recipeType = recipeType;
        this.progress = 0;
        this.maxProgress = maxProgress;
        this.maxProgressBase = maxProgress;
        this.progressPerTick = 1.0F;
        this.energyPerTick = energyPerTick;
        this.energyPerTickBase = energyPerTick;
        this.sound = sound;
        this.soundDuration = soundDuration;
        this.soundStart = 0;
    }

    protected void resetProgress() {
        progress = 0;
    }

    protected void increaseProgress() {
        progress++;
        energy.extractEnergy(energyPerTick, false);
    }

    protected boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    protected boolean hasEnergy() {
        return energy.getEnergyStored() >= energyPerTick;
    }

    protected boolean canInsertItemIntoSlot(int slot, Item item, int count) {
        return inventory.getStackInSlot(slot).isEmpty() || (inventory.getStackInSlot(slot).is(item)
                && inventory.getStackInSlot(slot).getCount() + count <= inventory
                        .getSlotLimit(slot));
    }

    protected void playSound() {
        if (sound == null || soundDuration == 0) {
            return;
        }
        if (!isWorking()) {
            return;
        }
        if (soundStart == 0) {
            soundStart = level.getGameTime();
        }
        if ((level.getGameTime() - soundStart) % soundDuration == 0) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    protected boolean isInputValid(int slot, ItemStack stack) {
        if (slot >= inputSlots) {
            return false;
        }
        return this.level.getRecipeManager().getAllRecipesFor(recipeType).stream()
                .anyMatch(recipe -> {
                    return recipe.value().getIngredients().stream().anyMatch(ingredient -> {
                        return ingredient.test(stack);
                    });
                });
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(NBT_TAG_PROGRESS, progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt(NBT_TAG_PROGRESS);
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public void setWorking(boolean working) {
        // TODO: Right now this only works if the block entity is attached to a
        // multiblock
        // controller. This should be changed to work with any block entity?
        if (level != null && getBlockState().getBlock() instanceof MultiBlockControllerBlock) {
            level.setBlockAndUpdate(worldPosition,
                    getBlockState().setValue(MultiBlockControllerBlock.WORKING, working));
        }
        if (!working) {
            soundStart = 0;
        }
    }

    public boolean isWorking() {
        // TODO: Same as for setWorking
        if (getBlockState().getBlock() instanceof MultiBlockControllerBlock) {
            return getBlockState().getValue(MultiBlockControllerBlock.WORKING);
        }
        return false;
    }

    @Override
    public boolean isUpgradeValid(UpgradeItem upgrade) {
        if (upgrade instanceof SpeedUpgradeItem) {
            return true;
        }
        return false;
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

    public boolean isFormed() {
        // We're going all in on multiblocks
        if (level != null) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof MultiBlockControllerBlock) {
                return state.getValue(MultiBlockControllerBlock.FORMED);
            }
        }
        return false;
    }

    @Override
    public IItemHandler getItemHandler(Direction direction) {
        if (isFormed()) {
            return super.getItemHandler(direction);
        }
        return null;
    }

    @Override
    public IEnergyStorage getEnergyHandler(Direction direction) {
        if (isFormed()) {
            return energyHandler.get();
        }
        return null;
    }
}
