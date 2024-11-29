package mwk.testmod.common.block.entity.base.processing;

import java.util.Optional;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.entity.modules.MachineModule;
import mwk.testmod.common.block.entity.modules.ProcessingModule;
import mwk.testmod.common.block.interfaces.ITickable;
import mwk.testmod.common.item.upgrades.SpeedUpgradeItem;
import mwk.testmod.common.item.upgrades.base.UpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A block entity that can process items in some way, e.g. a furnace or a generator.
 *
 * @param <I> The type of the recipe input.
 * @param <T> The type of the recipe.
 */
public abstract class ProcessingBlockEntity<I extends RecipeInput, T extends Recipe<I>> extends
        MachineBlockEntity implements ITickable {

    private Optional<ProcessingModule<I, T>> processingModule;

    protected ProcessingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
            RecipeType<T> recipeType, int maxProgress, int resourcePerTick) {
        super(type, pos, state);
        addModule(new ProcessingModule<>(this, recipeType, maxProgress, resourcePerTick,
                this::hasResource, this::consumeResource, this::getRecipeInput, this::isSameInput,
                this::canProcessRecipe, this::processRecipe));
    }

    @Override
    public void addModule(MachineModule module) {
        super.addModule(module);
        if (module instanceof ProcessingModule processing) {
            processingModule = Optional.of(processing);
        }
    }

    /**
     * Check if the block entity has the required resources to process the recipe, e.g. energy or
     * temporal flux.
     *
     * @return True if the block entity has the required resources, false otherwise.
     */
    protected boolean hasResource() {
        int resourcePerTick = processing().get().getResourcePerTick();
        if (energy().isPresent()) {
            return energy().get().getEnergyStored() >= resourcePerTick;
        }
        if (temporalFlux().isPresent()) {
            return temporalFlux().get().getTemporalFluxStored() >= resourcePerTick;
        }
        return false;
    }

    /**
     * Consume the resources required to process the recipe.
     */
    protected void consumeResource() {
        int resourcePerTick = processing().get().getResourcePerTick();
        if (energy().isPresent()) {
            energy().get().extractEnergy(resourcePerTick, false);
        }
        if (temporalFlux().isPresent()) {
            temporalFlux().get().extractTemporalFlux(resourcePerTick, false);
        }
    }

    /**
     * Return the recipe input that can be used to look up the recipe in the recipe manager, i.e.
     * the current state of the input slots and tanks.
     *
     * @return The recipe input.
     */
    protected abstract I getRecipeInput();

    /**
     * Return true if the two inputs are the same, i.e. the same items are in the input slots and
     * the same fluids are in the input tanks.
     *
     * @param input1 The first input.
     * @param input2 The second input.
     * @return True if the inputs are the same, false otherwise.
     */
    protected abstract boolean isSameInput(I input1, I input2);

    /**
     * Checks if the given recipe can be processed in the current state. This is mostly used for
     * crafting recipes to check if the result can be inserted into the output slot(s).
     *
     * @param recipe The recipe to check.
     * @return True if the recipe can be processed, false otherwise.
     */
    protected abstract boolean canProcessRecipe(T recipe);

    /**
     * This method is responsible for crafting the item. It should also handle the removal of the
     * input items and the insertion of the output items.
     *
     * @param recipe The recipe to craft.
     */
    protected abstract void processRecipe(T recipe);

    @Override
    protected void onInventoryChanged(int slot) {
        // TODO: This should only be called by the inventory module, thus it should always be
        // present
        if (slot < inventory().get().getInputSlots()) {
            processing().ifPresent(ProcessingModule::clearInputCache);
        }
    }

    @Override
    protected boolean isInputItemValid(int slot, ItemStack stack) {
        // TODO: This should only be called by the inventory module, thus it should always be
        // present
        if (slot >= inventory().get().getInputSlots()) {
            return false;
        }
        // TODO: Can we cache this?
        // TODO: For multi-input recipes this can't tell if the item is valid in the context
        // of the items already in the input slots
        if (processing().isPresent()) {
            ProcessingModule<I, T> processing = processing().get();
            return this.level.getRecipeManager().getAllRecipesFor(processing.getRecipeType())
                    .stream().anyMatch(recipe -> recipe.value().getIngredients().stream()
                            .anyMatch(ingredient -> ingredient.test(stack)));
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

    public Optional<ProcessingModule<I, T>> processing() {
        return processingModule;
    }
}
