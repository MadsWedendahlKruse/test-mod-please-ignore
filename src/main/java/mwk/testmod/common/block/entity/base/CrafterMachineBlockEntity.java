package mwk.testmod.common.block.entity.base;

import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block entity that can craft items using recipes.
 */
public abstract class CrafterMachineBlockEntity<T extends Recipe<Container>>
        extends BaseMachineBlockEntity implements ITickable, MenuProvider {

    public static final String NBT_TAG_PROGRESS = "progress";

    protected final RecipeType<T> recipeType;

    private int progress;
    private int maxProgress;
    private int energyPerTick;

    private final SoundEvent sound;
    private final int soundDuration; // in ticks
    private long soundStart; // in ticks

    protected CrafterMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, int inputSlots, int outputSlots, int maxProgress,
            RecipeType<T> recipeType, SoundEvent sound, int soundDuration) {
        super(type, pos, state, maxEnergy, EnergyType.CONSUMER, inputSlots, outputSlots);
        this.recipeType = recipeType;
        this.progress = 0;
        this.maxProgress = maxProgress;
        this.energyPerTick = energyPerTick;
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
        // TODO: Right now this only works if the block entity is attached to a multiblock
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
}
