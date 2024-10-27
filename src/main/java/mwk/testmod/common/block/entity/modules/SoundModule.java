package mwk.testmod.common.block.entity.modules;

import mwk.testmod.common.block.entity.base.processing.ProcessingBlockEntity;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class SoundModule implements MachineModule {

    private final ProcessingBlockEntity<?, ?> machine;
    private final SoundEvent sound;
    private final int soundDuration; // in ticks
    private long soundStart; // in ticks

    public SoundModule(ProcessingBlockEntity<?, ?> machine, SoundEvent sound, int soundDuration) {
        this.machine = machine;
        this.sound = sound;
        this.soundDuration = soundDuration;
    }

    @Override
    public void saveAdditional(CompoundTag tag, Provider registries) {

    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {

    }

    public void playSound() {
        if (sound == null || soundDuration == 0) {
            return;
        }
        if (!machine.isWorking()) {
            return;
        }
        Level level = machine.getLevel();
        if (soundStart == 0) {
            soundStart = level.getGameTime();
        }
        if ((level.getGameTime() - soundStart) % soundDuration == 0) {
            level.playSound(null, machine.getBlockPos(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public long getSoundStart() {
        return soundStart;
    }

    public void setSoundStart(long soundStart) {
        this.soundStart = soundStart;
    }
}
