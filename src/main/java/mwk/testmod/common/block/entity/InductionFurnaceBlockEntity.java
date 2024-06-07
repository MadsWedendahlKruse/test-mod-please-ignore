package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.entity.base.ParallelCrafterMachineBlockEntity;
import mwk.testmod.common.block.inventory.InductionFurnaceMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class InductionFurnaceBlockEntity extends ParallelCrafterMachineBlockEntity<BlastingRecipe> {

    public static final int INPUT_OUTPUT_SLOTS = 9;

    public InductionFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.INDUCTION_FURNACE_ENTITY_TYPE.get(), pos, state,
                TestModConfig.MACHINE_ENERGY_CAPACITY_DEFAULT.get(), 20, INPUT_OUTPUT_SLOTS, 6, 20,
                RecipeType.BLASTING, TestModSounds.MULTIBLOCK_INDUCTION_FURNACE.get(),
                TestModSounds.MULTIBLOCK_INDUCTION_FURNACE_DURATION);
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

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_INDUCTION_FURNACE;
    }
}
