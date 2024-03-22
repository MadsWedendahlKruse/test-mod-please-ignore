package mwk.testmod.common.block.entity;

import mwk.testmod.TestModConfig;
import mwk.testmod.client.animations.PerpetualAnimationFloat;
import mwk.testmod.common.block.entity.base.ParallelCrafterMachineBlockEntity;
import mwk.testmod.common.block.inventory.CrusherMenu;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.recipe.CrushingRecipe;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlockEntities;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

public class CrusherBlockEntity extends ParallelCrafterMachineBlockEntity<CrushingRecipe> {

    public static final float ROTOR_SPEED = (float) Math.PI; // [rad/s]

    private final PerpetualAnimationFloat rotorAnimation;

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(TestModBlockEntities.CRUSHER_BLOCK_ENTITY_TYPE.get(), pos, state,
                TestModConfig.ENERGY_CAPACITY_DEFAULT.get(), 20, 9, 20,
                CrushingRecipe.Type.INSTANCE, TestModSounds.MULTIBLOCK_CRUSHER.get(),
                // SoundEvents.BEACON_AMBIENT,
                TestModSounds.MULTIBLOCK_CRUSHER_DURATION);
        rotorAnimation = new PerpetualAnimationFloat(ROTOR_SPEED);
        rotorAnimation.start();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(TestModLanguageProvider.KEY_MULTIBLOCK_CRUSHER);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory,
            Player pPlayer) {
        return new CrusherMenu(pContainerId, pPlayer, worldPosition);
    }

    public void updateRotorAnimation() {
        rotorAnimation.update();
    }

    public float getRotorAngle() {
        return rotorAnimation.getValue();
    }

    // TODO: These three are duplicated in the MultiBlockPartBlockEntity

    public boolean isFormed() {
        if (level != null) {
            // BlockState state = level.getBlockState(worldPosition);
            if (getBlockState().getBlock() instanceof MultiBlockControllerBlock) {
                return getBlockState().getValue(MultiBlockControllerBlock.FORMED);
            }
        }
        return false;
    }

    @Override
    public IItemHandler getItemHandler(Direction direction) {
        if (isFormed() && direction == null) {
            return inventory;
        }
        return null;
    }

    @Override
    public IEnergyStorage getEnergyHandler(Direction direction) {
        if (isFormed() && direction == null) {
            return energyHandler.get();
        }
        return null;
    }
}
