package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.entity.modules.AutoIOModule;
import mwk.testmod.common.block.entity.modules.EnergyModule;
import mwk.testmod.common.block.entity.modules.EnergyModule.EnergyType;
import mwk.testmod.common.block.entity.modules.FluidTankModule;
import mwk.testmod.common.block.inventory.GeothermalGeneratorMenu;
import mwk.testmod.common.recipe.GeothermalGeneratorRecipe;
import mwk.testmod.common.recipe.inputs.FluidRecipeInput;
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
        extends GeneratorBlockEntity<FluidRecipeInput, GeothermalGeneratorRecipe> {

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.GEOTHERMAL_GENERATOR_ENTITY_TYPE.get(), pos, state,
                TestModConfig.GENERATOR_GEOTHERMAL_ENERGY_PER_TICK.get(),
                TestModRecipeTypes.GEOTHERMAL_GENERATOR.get(), null, 0);
        addModule(new EnergyModule(this, TestModConfig.GENERATOR_ENERGY_CAPACITY_DEFAULT.get(),
                EnergyType.PRODUCER));
        addModule(new FluidTankModule(this,
                new int[]{TestModConfig.GENERATOR_GEOTHERMAL_TANK_CAPACITY.get()},
                FluidTankModule.EMPTY_TANKS, this::isInputFluidValid));
        // TODO: Do we need IO module for this?
        addModule(new AutoIOModule());
    }

    @Override
    protected FluidRecipeInput getRecipeInput() {
        FluidStack stack = fluidTanks().map(fluidTanks -> fluidTanks.getFluidInTank(0))
                .orElse(FluidStack.EMPTY);
        return new FluidRecipeInput(stack);
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
