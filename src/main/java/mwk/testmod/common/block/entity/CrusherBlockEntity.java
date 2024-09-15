package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.client.animations.AnimationClock;
import mwk.testmod.common.block.entity.base.crafter.ParallelCrafterBlockEntity;
import mwk.testmod.common.block.inventory.CrusherMenu;
import mwk.testmod.common.recipe.CrushingRecipe;
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

public class CrusherBlockEntity extends ParallelCrafterBlockEntity<CrushingRecipe> {

    public static final float ROTOR_SPEED = (float) Math.PI; // [rad/s]
    private float rotorAngle;

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.CRUSHER_ENTITY_TYPE.get(), pos, state,
                TestModConfig.MACHINE_ENERGY_CAPACITY_DEFAULT.get(), 20, 9, 6, 40,
                TestModRecipeTypes.CRUSHING.get(), TestModSounds.CRUSHER.get(),
                TestModSounds.CRUSHER_DURATION);
    }

    @Override
    public Component getDisplayName() {
        return TestModBlocks.CRUSHER.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            Player player) {
        return new CrusherMenu(containerId, player, worldPosition);
    }

    @Override
    public String getDescriptionKey() {
        return TestModLanguageProvider.KEY_DESCRIPTION_CRUSHER;
    }

    public void updateRotorAngle() {
        rotorAngle += ROTOR_SPEED * AnimationClock.getInstance().getDeltaTime();
    }

    public float getRotorAngle() {
        return rotorAngle;
    }
}
