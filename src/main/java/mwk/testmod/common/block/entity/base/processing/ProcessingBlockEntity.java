package mwk.testmod.common.block.entity.base.processing;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * A block entity that can process items in some way, e.g. a furnace or a generator.
 */
public abstract class ProcessingBlockEntity<T extends Recipe<Container>> extends MachineBlockEntity
        implements ITickable {

    public static final String NBT_TAG_PROGRESS = "progress";

    protected final RecipeType<T> recipeType;
    // We only need to look up the recipe if the input slots have changed
    protected SimpleContainer latestInputs;
    protected T latestRecipe;

    protected int progress;
    protected int maxProgress;
    // Storing the progress per tick as a float makes applying upgrades easier
    protected float progressPerTick;
    protected int energyPerTick;
    // Base values before upgrades
    public final int maxProgressBase;
    public final int energyPerTickBase;

    private final SoundEvent sound;
    private final int soundDuration; // in ticks
    private long soundStart; // in ticks

    protected ProcessingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            int maxEnergy, int energyPerTick, EnergyType energyType, int inputSlots,
            int outputSlots, int upgradeSlots, int[] inputTankCapacities,
            int[] outputTankCapacities, int maxProgress, RecipeType<T> recipeType, SoundEvent sound,
            int soundDuration) {
        super(type, pos, state, maxEnergy, energyType, inputSlots, outputSlots, upgradeSlots,
                inputTankCapacities, outputTankCapacities);
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
    }

    protected void consumeEnergy() {
        energyStorage.extractEnergy(energyPerTick, false);
    }

    protected boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    protected boolean hasEnergy() {
        return energyStorage.getEnergyStored() >= energyPerTick;
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

    /**
     * This method should return the current recipe that can be crafted given the current inventory.
     * 
     * @return The current recipe that can be crafted.
     */
    protected T getCurrentRecipe() {
        SimpleContainer container = new SimpleContainer(this.inputSlots);
        for (int i = 0; i < this.inputSlots; i++) {
            container.setItem(i, this.inventory.getStackInSlot(i));
        }
        if (latestInputs != null) {
            // Check if the items in the input slots have changed
            boolean changed = false;
            for (int i = 0; i < this.inputSlots; i++) {
                if (!container.getItem(i).is(latestInputs.getItem(i).getItem())) {
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                return latestRecipe;
            }
        } else {
            latestInputs = container;
        }
        // Check if the items in the input slots match the latest recipe
        if (latestRecipe != null && latestRecipe.matches(container, level)) {
            return latestRecipe;
        }
        TestMod.LOGGER.debug("Getting new recipe");
        latestRecipe = this.level.getRecipeManager().getRecipeFor(this.recipeType, container, level)
                .map(RecipeHolder::value).orElse(null);
        latestInputs = container;
        return latestRecipe;
    }

    /**
     * This method checks if the given recipe can be processed in the current state. This is mostly
     * used for crafting recipes to check if the result can be inserted into the output slot(s).
     * 
     * @param recipe The recipe to check.
     * @return True if the recipe can be processed, false otherwise.
     */
    protected boolean canProcessRecipe(T recipe) {
        if (recipe == null) {
            return false;
        }
        ItemStack result = recipe.getResultItem(null);
        if (result.isEmpty()) {
            // If the item is empty we don't have to check if it can be inserted
            return true;
        }
        return canInsertItemIntoSlot(inputSlots, result.getItem(), result.getCount());
    }

    /**
     * This method is responsible for crafting the item. It should also handle the removal of the
     * input items and the insertion of the output items.
     * 
     * @param recipe The recipe to craft.
     */
    protected void processRecipe(T recipe) {
        for (int i = 0; i < inputSlots; i++) {
            this.inventory.extractItem(i, 1, false);
        }
        ItemStack result = recipe.getResultItem(null);
        if (result.isEmpty()) {
            return;
        }
        this.inventory.setStackInSlot(inputSlots, new ItemStack(result.getItem(),
                this.inventory.getStackInSlot(inputSlots).getCount() + result.getCount()));
    }

    @Override
    protected boolean isInputItemValid(int slot, ItemStack stack) {
        if (slot >= inputSlots) {
            return false;
        }
        // TODO: Can we cache this?
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
    public IEnergyStorage getEnergyStorage(Direction direction) {
        if (isFormed()) {
            return energyWrapper.get();
        }
        return null;
    }
}
