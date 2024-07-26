package mwk.testmod.common.block.entity;

import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.inventory.GeothermalGeneratorMenu;
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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

// TODO: Update to correct recipe type
public class GeothermalGeneratorBlockEntity extends GeneratorBlockEntity<RedstoneGeneratorRecipe> {

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.GEOTHERMAL_GENERATOR_ENTITY_TYPE.get(), pos, state, 100_000, 128,
                1, 0, 6, new int[] {10_000}, EMPTY_TANKS,
                TestModRecipeTypes.REDSTONE_GENERATOR.get(), null, 0);
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.GEOTHERMAL_GENERATOR.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            Player player) {
        return new GeothermalGeneratorMenu(containerId, player, worldPosition);
    }

    @Override
    public String getDescriptionKey() {
        // TODO: Update this to the correct key
        return TestModLanguageProvider.KEY_DESCRIPTION_REDSTONE_GENERATOR;
    }

    @Override
    protected boolean isInputFluidValid(int tank, FluidStack stack) {
        return stack.getFluid().isSame(Fluids.LAVA);
    }

}
