package mwk.testmod.common.util.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * A simple container that can store item stacks and fluid stacks. For most intents and purposes,
 * this behaves like a normal Container, but it also has a fluid container.
 */
public class SimpleItemFluidContainer implements Container {

    private final SimpleContainer itemContainer;
    private final SimpleFluidContainer fluidContainer;

    public SimpleItemFluidContainer(int itemSize, int fluidSize) {
        this.itemContainer = new SimpleContainer(itemSize);
        this.fluidContainer = new SimpleFluidContainer(fluidSize);
    }

    public SimpleItemFluidContainer(SimpleContainer itemContainer,
            SimpleFluidContainer fluidContainer) {
        this.itemContainer = itemContainer;
        this.fluidContainer = fluidContainer;
    }

    @Override
    public void setItem(int arg0, ItemStack arg1) {
        itemContainer.setItem(arg0, arg1);
    }

    @Override
    public ItemStack getItem(int arg0) {
        return itemContainer.getItem(arg0);
    }

    public void setFluid(int index, FluidStack stack) {
        fluidContainer.setFluid(index, stack);
    }

    public FluidStack getFluid(int index) {
        return fluidContainer.getFluid(index);
    }

    @Override
    public void clearContent() {
        itemContainer.clearContent();
    }

    @Override
    public int getContainerSize() {
        return itemContainer.getContainerSize();
    }


    @Override
    public boolean isEmpty() {
        return itemContainer.isEmpty();
    }

    @Override
    public ItemStack removeItem(int arg0, int arg1) {
        return itemContainer.removeItem(arg0, arg1);
    }

    @Override
    public ItemStack removeItemNoUpdate(int arg0) {
        return itemContainer.removeItemNoUpdate(arg0);
    }

    @Override
    public void setChanged() {
        itemContainer.setChanged();
    }

    @Override
    public boolean stillValid(Player arg0) {
        return itemContainer.stillValid(arg0);
    }

}
