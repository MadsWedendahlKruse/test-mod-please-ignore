package mwk.testmod.common.block.multiblock;

import java.util.Random;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.controller.MultiBlockControllerBlock;
import mwk.testmod.common.item.Wrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A block that is part of a multiblock structure.
 */
public class MultiBlockPartBlock extends Block implements EntityBlock, Wrenchable {

    // Whether or not the block is part of a formed multiblock structure.
    public static final BooleanProperty IS_FORMED = BooleanProperty.create("is_formed");
    // The number of particles to spawn when forming or unforming the multiblock structure.
    private static final int NUM_PARTICLES = 10;

    // Random number generator for particle effects.
    // TODO: Is this the best way to do this?
    private static Random random = new Random();

    public MultiBlockPartBlock(Properties properties) {
        // TODO: Not sure if noOcculsion is the best way to do this.
        super(properties.noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(IS_FORMED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(IS_FORMED);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if (pState.getValue(IS_FORMED)) {
            return true;
        }
        return super.propagatesSkylightDown(pState, pLevel, pPos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MultiBlockPartBlockEntity(pPos, pState);
    }

    /**
     * Get a random position on the surface of the block.
     * 
     * @param pos The position of the block.
     * @param offset The offset from the surface of the block.
     * @return A random position on the surface of the block.
     */
    public static double[] getRandomSurfacePosition(BlockPos pos, double offset) {
        double[] position = new double[3];
        // Pick a random face of the block.
        int face = random.nextInt(6);
        // Pick a random position on the face of the block.
        position[0] = pos.getX();
        position[1] = pos.getY();
        position[2] = pos.getZ();
        switch (face) {
            case 0:
                position[0] += random.nextDouble();
                position[1] += 1.0 + offset;
                position[2] += random.nextDouble();
                break;
            case 1:
                position[0] += random.nextDouble();
                position[1] += 0.0 - offset;
                position[2] += random.nextDouble();
                break;
            case 2:
                position[0] += random.nextDouble();
                position[1] += random.nextDouble();
                position[2] += 1.0 + offset;
                break;
            case 3:
                position[0] += random.nextDouble();
                position[1] += random.nextDouble();
                position[2] += 0.0 - offset;
                break;
            case 4:
                position[0] += 1.0 + offset;
                position[1] += random.nextDouble();
                position[2] += random.nextDouble();
                break;
            case 5:
                position[0] += 0.0 - offset;
                position[1] += random.nextDouble();
                position[2] += random.nextDouble();
                break;
            default:
                break;
        }
        return position;
    }

    /**
     * Spawn particles for forming or unforming the multiblock structure.
     * 
     * @param level The level the block is in.
     * @param pos The position of the block.
     * @param isFormed Whether or not the multiblock structure is formed.
     */
    private void spawnMultiBlockParticles(Level level, BlockPos pos, boolean isFormed) {
        for (int i = 0; i < NUM_PARTICLES; i++) {
            double[] position = getRandomSurfacePosition(pos, 0.075);
            SimpleParticleType particleType =
                    isFormed ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.CRIT;
            level.addParticle(particleType, position[0], position[1], position[2], 0.0f, 0.0f,
                    0.0f);
        }
    }

    /**
     * Play a sound for forming or unforming the multiblock structure.
     * 
     * @param level The level the block is in.
     * @param pos The position of the block.
     * @param isFormed Whether or not the multiblock structure is formed.
     */
    private void playMultiBlockSound(Level level, BlockPos pos, boolean isFormed) {
        SoundEvent soundEvent = isFormed ? SoundEvents.ANVIL_USE : SoundEvents.ANVIL_DESTROY;
        level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundEvent,
                SoundSource.BLOCKS, 0.5f, 0.8f, false);
    }

    /**
     * Set the formed state of the multi block part.
     * 
     * @param level The level the multi block part is in.
     * @param pos The position of the multi block part.
     * @param state The state of the multi block part.
     * @param isFormed The value of the formed state.
     * @param controllerPos The position of the controller multi block part.
     */
    public void setPartFormed(Level level, BlockPos pos, BlockState state, boolean isFormed,
            BlockPos controllerPos) {
        // Update block state on both sides
        level.setBlockAndUpdate(pos, state.setValue(IS_FORMED, isFormed));
        if (!level.isClientSide()) {
            // Update block entity on server side
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MultiBlockPartBlockEntity) {
                ((MultiBlockPartBlockEntity) blockEntity).setControllerPos(controllerPos);
            }
        } else {
            // Spawn particles on client side
            // TODO: Consider only spanwing particles on the faces that are exposed to air.
            // I'm not sure if this is a premature optimization.
            spawnMultiBlockParticles(level, pos, isFormed);
            // Only play the sound for the controller, otherwise we get a sound from each block
            if (state.getBlock() instanceof MultiBlockControllerBlock) {
                playMultiBlockSound(level, pos, isFormed);
            }
        }
    }

    /**
     * Get the position of the multilblock controller.
     * 
     * @param level The level the multiblock part is in.
     * @param partPos The position of the multiblock part.
     * @return The position of the multilblock controller.
     */
    private BlockPos getControllerPos(Level level, BlockPos partPos) {
        BlockEntity blockEntity = level.getBlockEntity(partPos);
        if (blockEntity instanceof MultiBlockPartBlockEntity) {
            return ((MultiBlockPartBlockEntity) blockEntity).getControllerPos();
        }
        return null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState,
            boolean movedByPiston) {
        TestMod.LOGGER.debug("MultiBlockPartBlock::onRemove");
        TestMod.LOGGER.debug("state = " + state);
        TestMod.LOGGER.debug("state @ " + pos + " = " + level.getBlockState(pos));
        TestMod.LOGGER.debug("newState = " + newState);
        TestMod.LOGGER.debug("blockEntity @ " + pos + " = " + level.getBlockEntity(pos));
        // If any of the blocks in the multiblock are removed while the multiblock is
        // formed we have to unform it
        if (state.getValue(IS_FORMED)) {
            BlockPos controllerPos = getControllerPos(level, pos);
            if (controllerPos != null) {
                BlockState controllerState = level.getBlockState(controllerPos);
                if (controllerState.getBlock() instanceof MultiBlockControllerBlock) {
                    ((MultiBlockControllerBlock) controllerState.getBlock()).setMultiblockFormed(
                            level, controllerPos, controllerState, false, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        TestMod.LOGGER.debug("MultiBlockPartBlock::use");
        // If the multiblock structure is formed, propagate the use event to the
        // multiblock controller.
        if (state.getValue(IS_FORMED)) {
            BlockPos controllerPos = getControllerPos(level, pos);
            if (controllerPos != null) {
                BlockState controllerState = level.getBlockState(controllerPos);
                if (controllerState.getBlock() instanceof MultiBlockControllerBlock) {
                    return ((MultiBlockControllerBlock) controllerState.getBlock())
                            .use(controllerState, level, controllerPos, player, hand, hit);
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean onWrenched(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand) {
        // Check if the super onWrenched method does anything.
        if (Wrenchable.super.onWrenched(state, level, pos, player, hand)) {
            return true;
        }
        // If not, and the multiblock structure is formed, propagate the wrenched
        // event to the multiblock controller.
        if (state.getValue(IS_FORMED)) {
            BlockPos controllerPos = getControllerPos(level, pos);
            if (controllerPos != null) {
                BlockState controllerState = level.getBlockState(controllerPos);
                if (controllerState.getBlock() instanceof MultiBlockControllerBlock) {
                    return ((MultiBlockControllerBlock) controllerState.getBlock())
                            .onWrenched(controllerState, level, controllerPos, player, hand);
                }
            }
        }
        return false;
    }
}
