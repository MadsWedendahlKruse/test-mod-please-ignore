package mwk.testmod.common.block.entity.base;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class OneToOneCrafterMachineBlockEntity<T extends Recipe<Container>>
        extends SingleCrafterMachineBlockEntity<T> {

    private static final int INPUT_SLOT_INDEX = 0;
    private static final int OUTPUT_SLOT_INDEX = 1;

    protected OneToOneCrafterMachineBlockEntity(BlockEntityType<?> type, BlockPos pos,
            BlockState state, int maxEnergy, int energyPerTick, int maxProgress,
            RecipeType<T> recipeType) {
        super(type, pos, state, maxEnergy, energyPerTick, 1, 1, maxProgress, recipeType);
    }

    @Override
    protected Optional<RecipeHolder<T>> getCurrentRecipe() {
        SimpleContainer container = new SimpleContainer(this.inputSlots);
        for (int i = 0; i < this.inputSlots; i++) {
            container.setItem(i, this.inventory.getStackInSlot(i));
        }
        return this.level.getRecipeManager().getRecipeFor(this.recipeType, container, level);
    }

    @Override
    protected boolean isRecipeValid(Optional<RecipeHolder<T>> recipe) {
        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().value().getResultItem(null);
        return canInsertItemIntoSlot(OUTPUT_SLOT_INDEX, result.getItem(), result.getCount());
    }

    @Override
    protected void craftItem(Optional<RecipeHolder<T>> recipe) {
        ItemStack result = recipe.get().value().getResultItem(null);
        this.inventory.extractItem(INPUT_SLOT_INDEX, 1, false);
        this.inventory.setStackInSlot(OUTPUT_SLOT_INDEX, new ItemStack(result.getItem(),
                this.inventory.getStackInSlot(OUTPUT_SLOT_INDEX).getCount() + result.getCount()));
    }

    @Override
    public abstract Component getDisplayName();

    @Override
    public abstract AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory,
            Player pPlayer);
}
