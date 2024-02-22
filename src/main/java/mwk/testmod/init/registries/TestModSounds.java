package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModSounds {

    private TestModSounds() {}

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, TestMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MULTI_BLOCK_FORM = SOUND_EVENTS
            .register("block.multiblock_form", () -> SoundEvent.createVariableRangeEvent(
                    new ResourceLocation(TestMod.MODID, "block.multiblock_form")));

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}
