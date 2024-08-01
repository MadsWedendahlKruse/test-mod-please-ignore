package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.client.animations.PerpetualAnimationFloat;
import mwk.testmod.common.block.entity.base.generator.GeneratorBlockEntity;
import mwk.testmod.common.block.inventory.StirlingGeneratorMenu;
import mwk.testmod.common.recipe.StirlingGeneratorRecipe;
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

public class StirlingGeneratorBlockEntity extends GeneratorBlockEntity<StirlingGeneratorRecipe> {

    private static final float FLYWHEEL_SPEED = (float) (2 * Math.PI); // [rad/s]
    private final PerpetualAnimationFloat flywheelAnimation;

    public StirlingGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.STIRLING_GENERATOR_ENTITY_TYPE.get(), pos, state,
                TestModConfig.GENERATOR_ENERGY_CAPACITY_DEFAULT.get(),
                TestModConfig.GENERATOR_STIRLING_ENERGY_PER_TICK.get(), 1, 0, 6, EMPTY_TANKS,
                EMPTY_TANKS, TestModRecipeTypes.STIRLING_GENERATOR.get(), null, 0);
        flywheelAnimation = new PerpetualAnimationFloat(FLYWHEEL_SPEED);
        flywheelAnimation.start();
    }


    @Override
    public Component getDisplayName() {
        return TestModBlocks.STIRLING_GENERATOR.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            Player player) {
        return new StirlingGeneratorMenu(containerId, player, worldPosition);
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_STIRLING_GENERATOR;
    }

    public void updateFlywheelAnimation() {
        flywheelAnimation.update();
    }

    public float getFlywheelAngle() {
        return flywheelAnimation.getValue();
    }

}
