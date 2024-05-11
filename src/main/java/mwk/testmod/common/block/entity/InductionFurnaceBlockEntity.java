package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.entity.base.ParallelCrafterMachineBlockEntity;
import mwk.testmod.common.block.inventory.InductionFurnaceMenu;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

public class InductionFurnaceBlockEntity extends ParallelCrafterMachineBlockEntity<BlastingRecipe> {

    public static final int INPUT_OUTPUT_SLOTS = 9;

    public InductionFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.INDUCTION_FURNACE_ENTITY_TYPE.get(), pos, state,
                TestModConfig.MACHINE_ENERGY_CAPACITY_DEFAULT.get(), 20, INPUT_OUTPUT_SLOTS, 6, 20,
                RecipeType.BLASTING, TestModSounds.MULTIBLOCK_INDUCTION_FURNACE.get(),
                TestModSounds.MULTIBLOCK_INDUCTION_FURNACE_DURATION);
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
            return super.getItemHandler(direction);
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
        return TestModBlocks.INDUCTION_FURNACE.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory,
            Player pPlayer) {
        return new InductionFurnaceMenu(pContainerId, pPlayer, worldPosition);
    }
}
