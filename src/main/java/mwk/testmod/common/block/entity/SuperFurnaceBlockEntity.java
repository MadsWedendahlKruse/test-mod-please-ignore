package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.entity.base.ParallelCrafterMachineBlockEntity;
import mwk.testmod.common.block.inventory.SuperFurnaceMenu;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

public class SuperFurnaceBlockEntity extends ParallelCrafterMachineBlockEntity<SmeltingRecipe> {

    public static final int INPUT_OUTPUT_SLOTS = 9;

    public SuperFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.SUPER_FURNACE_BLOCK_ENTITY_TYPE.get(), pos, state,
                TestModConfig.ENERGY_CAPACITY_DEFAULT.get(), 20, INPUT_OUTPUT_SLOTS, 20,
                RecipeType.SMELTING, SoundEvents.FURNACE_FIRE_CRACKLE, 20);
    }

    // TODO: These three are duplicated in the MultiBlockPartBlockEntity

    private boolean isFormed() {
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() instanceof MultiBlockControllerBlock) {
                return state.getValue(MultiBlockControllerBlock.FORMED);
            }
        }
        return false;
    }

    @Override
    public IItemHandler getItemHandler(Direction direction) {
        if (isFormed() && direction == null) {
            return inventory;
        }
        return null;
    }

    @Override
    public IEnergyStorage getEnergyHandler(Direction direction) {
        if (isFormed() && direction == null) {
            return energyHandler.get();
        }
        return null;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(TestModLanguageProvider.KEY_MULTIBLOCK_SUPER_FURNACE);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory,
            Player pPlayer) {
        return new SuperFurnaceMenu(pContainerId, pPlayer, worldPosition);
    }
}
