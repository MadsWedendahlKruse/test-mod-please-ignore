package mwk.testmod.common.block.multiblock;

import java.util.function.BiFunction;
import mwk.testmod.common.block.interfaces.IWrenchable;
import mwk.testmod.common.block.multiblock.entity.MultiBlockPartBlockEntity;
import mwk.testmod.common.util.RandomUtils;
import mwk.testmod.init.registries.TestModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A block that is part of a multiblock structure.
 */
public class MultiBlockPartBlock extends Block implements EntityBlock, IWrenchable {

    // Whether or not the block is part of a formed multiblock structure.
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    // The number of particles to spawn when forming or unforming the multiblock structure.
    private static final int NUM_PARTICLES = 10;

    public MultiBlockPartBlock(Properties properties) {
        // TODO: Not sure if noOcculsion is the best way to do this.
        super(properties.noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(FORMED, false));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FORMED);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        if (pState.getValue(FORMED)) {
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
     * @param pos    The position of the block.
     * @param offset The offset from the surface of the block.
     * @return A random position on the surface of the block.
     */
    public static double[] getRandomSurfacePosition(BlockPos pos, double offset) {
        double[] position = new double[3];
        // Pick a random face of the block.
        int face = RandomUtils.RANDOM.nextInt(6);
        // Pick a random position on the face of the block.
        position[0] = pos.getX();
        position[1] = pos.getY();
        position[2] = pos.getZ();
        switch (face) {
            case 0:
                position[0] += RandomUtils.RANDOM.nextDouble();
                position[1] += 1.0 + offset;
                position[2] += RandomUtils.RANDOM.nextDouble();
                break;
            case 1:
                position[0] += RandomUtils.RANDOM.nextDouble();
                position[1] += 0.0 - offset;
                position[2] += RandomUtils.RANDOM.nextDouble();
                break;
            case 2:
                position[0] += RandomUtils.RANDOM.nextDouble();
                position[1] += RandomUtils.RANDOM.nextDouble();
                position[2] += 1.0 + offset;
                break;
            case 3:
                position[0] += RandomUtils.RANDOM.nextDouble();
                position[1] += RandomUtils.RANDOM.nextDouble();
                position[2] += 0.0 - offset;
                break;
            case 4:
                position[0] += 1.0 + offset;
                position[1] += RandomUtils.RANDOM.nextDouble();
                position[2] += RandomUtils.RANDOM.nextDouble();
                break;
            case 5:
                position[0] += 0.0 - offset;
                position[1] += RandomUtils.RANDOM.nextDouble();
                position[2] += RandomUtils.RANDOM.nextDouble();
                break;
            default:
                break;
        }
        return position;
    }

    /**
     * Spawn particles for forming or unforming the multiblock structure.
     *
     * @param level    The level the block is in.
     * @param pos      The position of the block.
     * @param isFormed Whether or not the multiblock structure is formed.
     */
    private void spawnMultiBlockParticles(Level level, BlockPos pos, boolean isFormed) {
        for (int i = 0; i < NUM_PARTICLES; i++) {
            double[] position = getRandomSurfacePosition(pos, 0.075);
            SimpleParticleType particleType =
                    isFormed ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.CRIT;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(particleType, position[0], position[1], position[2], 1,
                        0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    /**
     * Play a sound for forming or unforming the multiblock structure.
     *
     * @param level    The level the block is in.
     * @param pos      The position of the block.
     * @param isFormed Whether or not the multiblock structure is formed.
     */
    private void playMultiBlockSound(Level level, BlockPos pos, boolean isFormed) {
        SoundEvent soundEvent =
                isFormed ? TestModSounds.MULTIBLOCK_FORM.get() : SoundEvents.ANVIL_DESTROY;
        level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * Set the formed state of the multi block part.
     *
     * @param level         The level the multi block part is in.
     * @param pos           The position of the multi block part.
     * @param state         The state of the multi block part.
     * @param isFormed      The value of the formed state.
     * @param controllerPos The position of the controller multi block part.
     */
    public void setPartFormed(Level level, BlockPos pos, BlockState state, boolean isFormed,
            BlockPos controllerPos) {
        // Update block state on both sides
        if (level.getBlockState(pos).getBlock() instanceof MultiBlockPartBlock) {
            level.setBlockAndUpdate(pos, state.setValue(FORMED, isFormed));
        }
        // Update block entity on both sides
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MultiBlockPartBlockEntity multiBlockEntity) {
            multiBlockEntity.setControllerPos(controllerPos);
            // TODO: Only do this if the entity has capabilities
            blockEntity.invalidateCapabilities();
        }
        // The rest is handled on the server side
        if (level.isClientSide()) {
            return;
        }
        // Only play the sound for the controller, otherwise we get a sound from each block
        if (state.getBlock() instanceof MultiBlockControllerBlock) {
            playMultiBlockSound(level, pos, isFormed);
        }
        // TODO: Consider only spanwing particles on the faces that are exposed to air.
        // I'm not sure if this is a premature optimization.
        spawnMultiBlockParticles(level, pos, isFormed);
    }

    /**
     * Get the position of the multilblock controller.
     *
     * @param level   The level the multiblock part is in.
     * @param partPos The position of the multiblock part.
     * @return The position of the multilblock controller.
     */
    private BlockPos getControllerPos(BlockGetter level, BlockPos partPos) {
        BlockEntity blockEntity = level.getBlockEntity(partPos);
        if (blockEntity instanceof MultiBlockPartBlockEntity) {
            return ((MultiBlockPartBlockEntity) blockEntity).getControllerPos();
        }
        return null;
    }

    /**
     * Propagate an action to the controller of the multiblock structure.
     *
     * @param level        The level the multiblock part is in.
     * @param pos          The position of the multiblock part.
     * @param action       The action to propagate.
     * @param defaultValue The default value to return if the controller is not found.
     * @param <R>          The type of the return value.
     * @return The return value of the action.
     */
    private <R> R propagateToController(BlockGetter level, BlockPos pos,
            BiFunction<MultiBlockControllerBlock, BlockPos, R> action, R defaultValue) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof MultiBlockPartBlock && state.getValue(FORMED)) {
            BlockPos controllerPos = getControllerPos(level, pos);
            if (controllerPos != null && !controllerPos.equals(pos)) {
                BlockState controllerState = level.getBlockState(controllerPos);
                if (controllerState.getBlock() instanceof MultiBlockControllerBlock controller) {
                    return action.apply(controller, controllerPos);
                }
            }
        }
        return defaultValue;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState,
            boolean movedByPiston) {
        // If any of the blocks in the multiblock are removed while the multiblock is
        // formed we have to unform it
        if (state.getValue(FORMED)) {
            // Check if the old state is from the controller
            // When the controller is broken the block state according to the level is
            // already the new state, so the below attempt to unform will fail
            if (state.getBlock() instanceof MultiBlockControllerBlock controller) {
                controller.setMultiblockFormed(level, pos, state, false, false);
            } else {
                // If it's not, we have to find the controller and unform it
                propagateToController(level, pos, (controller, controllerPos) -> {
                    controller.setMultiblockFormed(level, controllerPos,
                            level.getBlockState(controllerPos), false, false);
                    return null;
                }, null);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        return propagateToController(level, pos, (controller, controllerPos) ->
                        controller.useWithoutItem(level.getBlockState(controllerPos), level, controllerPos,
                                player, hit),
                super.useWithoutItem(state, level, pos, player, hit));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return propagateToController(level, pos, (controller, controllerPos) ->
                        controller.useItemOn(stack, level.getBlockState(controllerPos), level,
                                controllerPos, player, hand, hitResult),
                super.useItemOn(stack, state, level, pos, player, hand, hitResult));
    }

    @Override
    public boolean onWrenched(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, Vec3 clickLocation) {
        if (IWrenchable.super.onWrenched(state, level, pos, player, hand, clickLocation)) {
            return true;
        }
        return propagateToController(level, pos, (controller, controllerPos) ->
                        controller.onWrenched(level.getBlockState(controllerPos), level, controllerPos,
                                player, hand, clickLocation),
                false);
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return propagateToController(level, pos, (controller, controllerPos) -> {
            VoxelShape controllerShape = controller.getShape(level.getBlockState(controllerPos),
                    level, controllerPos, context);
            BlockPos controllerOffset = controllerPos.subtract(pos);
            return controllerShape.move(controllerOffset.getX(), controllerOffset.getY(),
                    controllerOffset.getZ());
        }, super.getShape(state, level, pos, context));
    }

}
