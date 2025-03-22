package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.entity.base.crafter.ParallelCrafterBlockEntity;
import mwk.testmod.common.block.inventory.ElectricFurnaceMenu;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceBlockEntity extends ParallelCrafterBlockEntity<SmeltingRecipe> {

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.ELECTRIC_FURNACE_ENTITY_TYPE.get(), pos, state,
                TestModConfig.MACHINE_ENERGY_CAPACITY_DEFAULT.get(),
                50, 4, 2, 80,
                RecipeType.SMELTING, null, 0);
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_ELECTRIC_FURNACE;
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.ELECTRIC_FURNACE.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ElectricFurnaceMenu(containerId, player, this.worldPosition);
    }
}
