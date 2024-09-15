package mwk.testmod.common.block.entity.base.processing;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.item.upgrades.SpeedUpgradeItem;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import mwk.testmod.common.recipe.base.FluidRecipe;
import mwk.testmod.common.util.inventory.SimpleFluidContainer;
import mwk.testmod.common.util.inventory.SimpleItemFluidContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * A block entity that can process items in some way, e.g. a furnace or a generator.
 */
public abstract class ProcessingBlockEntity<T extends Recipe<Container>> extends MachineBlockEntity
        implements ITickable {

    public static final String NBT_TAG_PROGRESS = "progress";

    protected final RecipeType<T> recipeType;
    // We only need to look up the recipe if the input slots/tank have changed
    protected Container latestItemInputs;
    protected SimpleFluidContainer latestFluidInputs;
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

    protected boolean canInsertFluidIntoTank(int tank, FluidStack fluid) {
        return fluidTanks.getFluidInTank(tank).isEmpty()
                || fluidTanks.getFluidInTank(tank).isFluidEqual(fluid)
                && fluidTanks.getFluidInTank(tank).getAmount()
                + fluid.getAmount() <= fluidTanks.getTankCapacity(tank);
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

    private SimpleContainer createItemContainer() {
        SimpleContainer itemContainer = new SimpleContainer(this.inputSlots);
        for (int i = 0; i < this.inputSlots; i++) {
            itemContainer.setItem(i, this.inventory.getStackInSlot(i));
        }
        return itemContainer;
    }

    private SimpleFluidContainer createFluidContainer(boolean copy) {
        SimpleFluidContainer fluidContainer = new SimpleFluidContainer(this.inputTanks);
        for (int i = 0; i < this.inputTanks; i++) {
            if (copy) {
                fluidContainer.setFluid(i, this.fluidTanks.getFluidInTank(i).copy());
            } else {
                fluidContainer.setFluid(i, this.fluidTanks.getFluidInTank(i));
            }
        }
        return fluidContainer;
    }

    private boolean itemInputsChanged(SimpleContainer itemContainer) {
        if (latestItemInputs == null) {
            return true;
        }
        for (int i = 0; i < this.inputSlots; i++) {
            ItemStack containerItem = itemContainer.getItem(i);
            ItemStack latestItem = latestItemInputs.getItem(i);
            if (!containerItem.is(latestItem.getItem())) {
                return true;
            }
            // TODO: We need something like this to handle recipes where the count of the input
            // items matters
            // if (containerItem.getCount() != latestItem.getCount()) {
            // return true;
            // }
        }
        return false;
    }

    private boolean fluidInputsChanged(SimpleFluidContainer fluidContainer) {
        if (latestFluidInputs == null) {
            return true;
        }
        for (int i = 0; i < fluidContainer.getSize(); i++) {
            FluidStack containerFluid = fluidContainer.getFluid(i);
            FluidStack latestFluid = latestFluidInputs.getFluid(i);
            if (!containerFluid.isFluidEqual(latestFluid)) {
                return true;
            }
            if (containerFluid.getAmount() != latestFluid.getAmount()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method should return the current recipe that can be crafted given the current
     * inventory.
     *
     * @return The current recipe that can be crafted.
     */
    protected T getCurrentRecipe() {
        SimpleContainer itemContainer = createItemContainer();
        SimpleFluidContainer fluidContainer = createFluidContainer(false);
        // Check if the input items or fluids have changed
        if (!(itemInputsChanged(itemContainer) || fluidInputsChanged(fluidContainer))) {
            return latestRecipe;
        }
        // Check if the input items and fluids match the latest recipe
        SimpleItemFluidContainer combinedContainer =
                new SimpleItemFluidContainer(itemContainer, fluidContainer);
        if (latestRecipe != null && latestRecipe.matches(combinedContainer, level)) {
            return latestRecipe;
        }
        latestRecipe = this.level.getRecipeManager()
                .getRecipeFor(this.recipeType, combinedContainer, level).map(RecipeHolder::value)
                .orElse(null);
        TestMod.LOGGER.debug("Got new recipe from recipe manager: " + latestRecipe);
        latestItemInputs = itemContainer;
        // TODO: We need to copy the fluid stack here, otherwise we end up saving references to the
        // input tanks in the latestFluidInputs, and then they're always identical. We don't need to
        // do this for the item stacks, but I don't know why. For performance reasons we only copy
        // the fluid when saving the latestFluidInputs
        latestFluidInputs = createFluidContainer(true);
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
        ItemStack itemResult = recipe.getResultItem(null);
        boolean canInsertItem = itemResult.isEmpty()
                || canInsertItemIntoSlot(outputSlots, itemResult.getItem(), itemResult.getCount());
        boolean canInsertFluid = true;
        if (recipe instanceof FluidRecipe fluidRecipe) {
            FluidStack fluidResult = fluidRecipe.getFluidResult();
            canInsertFluid =
                    fluidResult.isEmpty() || canInsertFluidIntoTank(outputTanks, fluidResult);
        }
        return canInsertItem && canInsertFluid;
    }

    /**
     * This method is responsible for crafting the item. It should also handle the removal of the
     * input items and the insertion of the output items.
     *
     * @param recipe The recipe to craft.
     */
    protected void processRecipe(T recipe) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            // TODO: Ingredients can have multiple items???
            ItemStack ingredient = ingredients.get(i).getItems()[0];
            this.inventory.extractItem(i, ingredient.getCount(), false);
        }
        ItemStack result = recipe.getResultItem(null);
        if (!result.isEmpty()) {
            this.inventory.setStackInSlot(inputSlots, new ItemStack(result.getItem(),
                    this.inventory.getStackInSlot(inputSlots).getCount() + result.getCount()));
        }
        if (recipe instanceof FluidRecipe fluidRecipe) {
            NonNullList<FluidStack> fluidIngredients = fluidRecipe.getFluidIngredients();
            for (int i = 0; i < inputTanks; i++) {
                this.fluidTanks.drain(i, fluidIngredients.get(i), FluidAction.EXECUTE);
            }
            FluidStack fluidResult = fluidRecipe.getFluidResult();
            if (!fluidResult.isEmpty()) {
                this.fluidTanks.fill(inputTanks, fluidResult, FluidAction.EXECUTE);
            }
        }
    }

    @Override
    protected void onInventoryChanged(int slot) {
        if (slot < inputSlots) {
            latestItemInputs = null;
        }
    }

    @Override
    protected boolean isInputItemValid(int slot, ItemStack stack) {
        if (slot >= inputSlots) {
            return false;
        }
        // TODO: Can we cache this?
        return this.level.getRecipeManager().getAllRecipesFor(recipeType).stream()
                .anyMatch(recipe -> recipe.value().getIngredients().stream()
                        .anyMatch(ingredient -> ingredient.test(stack)));
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
