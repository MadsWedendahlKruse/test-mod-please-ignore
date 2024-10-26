package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.entity.modules.AutoIOModule;
import mwk.testmod.common.block.entity.modules.EnergyModule;
import mwk.testmod.common.block.entity.modules.EnergyModule.EnergyType;
import mwk.testmod.common.block.entity.modules.InventoryModule;
import mwk.testmod.common.block.inventory.RedstoneGeneratorMenu;
import mwk.testmod.common.recipe.RedstoneGeneratorRecipe;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneGeneratorBlockEntity extends
        GeneratorBlockEntity<SingleRecipeInput, RedstoneGeneratorRecipe> {

    public RedstoneGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.REDSTONE_GENERATOR_ENTITY_TYPE.get(), pos, state,
                TestModConfig.GENERATOR_REDSTONE_ENERGY_PER_TICK.get(),
                TestModRecipeTypes.REDSTONE_GENERATOR.get(), null, 0);
        addModule(new EnergyModule(this, TestModConfig.GENERATOR_ENERGY_CAPACITY_DEFAULT.get(),
                EnergyType.PRODUCER));
        addModule(new InventoryModule(this, 1, 0, 6, this::onInventoryChanged,
                this::isInputItemValid));
        addModule(new AutoIOModule());
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.REDSTONE_GENERATOR.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            Player player) {
        return new RedstoneGeneratorMenu(containerId, player, worldPosition);
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_REDSTONE_GENERATOR;
    }

    @Override
    protected SingleRecipeInput getRecipeInput() {
        ItemStack stack = inventory().map(inventory -> inventory.getStackInSlot(0))
                .orElse(ItemStack.EMPTY);
        return new SingleRecipeInput(stack);
    }
}
