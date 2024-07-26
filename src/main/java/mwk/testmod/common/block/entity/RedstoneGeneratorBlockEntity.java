package mwk.testmod.common.block.entity;

import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
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
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneGeneratorBlockEntity extends GeneratorBlockEntity<RedstoneGeneratorRecipe> {

    public RedstoneGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.REDSTONE_GENERATOR_ENTITY_TYPE.get(), pos, state, 100_000, 128,
                1, 0, 6, EMPTY_TANKS, EMPTY_TANKS, TestModRecipeTypes.REDSTONE_GENERATOR.get(),
                null, 0);
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

}
