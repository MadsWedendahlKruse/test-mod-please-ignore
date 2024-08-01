package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.inventory.GeothermalGeneratorMenu;
import mwk.testmod.common.recipe.GeothermalGeneratorRecipe;
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

public class GeothermalGeneratorBlockEntity
        extends GeneratorBlockEntity<GeothermalGeneratorRecipe> {

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.GEOTHERMAL_GENERATOR_ENTITY_TYPE.get(), pos, state,
                TestModConfig.GENERATOR_ENERGY_CAPACITY_DEFAULT.get(),
                TestModConfig.GENERATOR_GEOTHERMAL_ENERGY_PER_TICK.get(), 0, 0, 6,
                new int[] {TestModConfig.GENERATOR_GEOTHERMAL_TANK_CAPACITY.get()}, EMPTY_TANKS,
                TestModRecipeTypes.GEOTHERMAL_GENERATOR.get(), null, 0);
    }

    @Override
    protected GeothermalGeneratorRecipe getCurrentRecipe() {
        return super.getCurrentRecipe();
    }

    @Override
    protected void processRecipe(GeothermalGeneratorRecipe recipe) {
        super.processRecipe(recipe);
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
        return TestModLanguageProvider.KEY_DESCRIPTION_GEOTHERMAL_GENERATOR;
    }

    @Override
    protected boolean isInputFluidValid(int tank, FluidStack stack) {
        return stack.getFluid().isSame(Fluids.LAVA);
    }

}
