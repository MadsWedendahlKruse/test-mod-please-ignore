package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.client.animations.AnimationClock;
import mwk.testmod.common.block.entity.base.crafter.OneToManyCrafterBlockEntity;
import mwk.testmod.common.block.inventory.SeparatorMenu;
import mwk.testmod.common.recipe.SeparationRecipe;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModBlocks;
import mwk.testmod.init.registries.TestModRecipeTypes;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class SeparatorBlockEntity extends OneToManyCrafterBlockEntity<SeparationRecipe> {

    public static final float SPINNER_SPEED = (float) (2 * Math.PI); // [rad/s]
    private float spinnerAngle;

    public SeparatorBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.SEPARATOR_ENTITY_TYPE.get(), pos, state,
                TestModConfig.MACHINE_ENERGY_CAPACITY_DEFAULT.get(), 20, 1, 3, 6, 20,
                TestModRecipeTypes.SEPARATION.get(), TestModSounds.MULTIBLOCK_CRUSHER.get(),
                TestModSounds.MULTIBLOCK_CRUSHER_DURATION);
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.SEPARATOR.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            Player player) {
        return new SeparatorMenu(containerId, player, worldPosition);
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_SEPARATOR;
    }

    public void updateSpinnerAngle() {
        spinnerAngle += SPINNER_SPEED * AnimationClock.getInstance().getDeltaTime();
    }

    public float getSpinnerAngle() {
        return spinnerAngle;
    }
}
