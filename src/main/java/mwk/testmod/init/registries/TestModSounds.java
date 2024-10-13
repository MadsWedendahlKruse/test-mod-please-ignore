package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModSounds {

    private TestModSounds() {
    }

    public static final String MULTIBLOCK_FORM_ID = "block.multiblock_form";
    public static final int MULTIBLOCK_FORM_RANGE = 32; // [blocks]
    public static final String CRUSHER_ID = "block.crusher";
    public static final int CRUSHER_DURATION = 20; // [ticks]
    public static final int CRUSHER_RANGE = 32; // [blocks]
    public static final String INDUCTION_FURNACE_ID = "block.induction_furnace";
    public static final int INDUCTION_FURNACE_DURATION = 10; // [ticks]
    public static final int INDUCTION_FURNACE_RANGE = 16; // [blocks]
    public static final String STAMPING_PRESS_ID = "block.stamping_press";
    public static final int STAMPING_PRESS_DURATION = 20; // [ticks]
    public static final int STAMPING_PRESS_RANGE = 32; // [blocks]
    public static final String STAMPING_PRESS_PISTON_ID = "block.stamping_press_piston";
    public static final int STAMPING_PRESS_PISTON_RANGE = 32; // [blocks]

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
            Registries.SOUND_EVENT, TestMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MULTIBLOCK_FORM = registerFixedRangeSound(
            MULTIBLOCK_FORM_ID, MULTIBLOCK_FORM_RANGE);
    public static final DeferredHolder<SoundEvent, SoundEvent> CRUSHER = registerFixedRangeSound(
            CRUSHER_ID, CRUSHER_RANGE);
    public static final DeferredHolder<SoundEvent, SoundEvent> INDUCTION_FURNACE = registerFixedRangeSound(
            INDUCTION_FURNACE_ID, INDUCTION_FURNACE_RANGE);
    public static final DeferredHolder<SoundEvent, SoundEvent> STAMPING_PRESS = registerFixedRangeSound(
            STAMPING_PRESS_ID, STAMPING_PRESS_RANGE);
    public static final DeferredHolder<SoundEvent, SoundEvent> STAMPING_PRESS_PISTON = registerFixedRangeSound(
            STAMPING_PRESS_PISTON_ID, STAMPING_PRESS_PISTON_RANGE);

    private static DeferredHolder<SoundEvent, SoundEvent> registerFixedRangeSound(String id,
            int range) {
        return SOUND_EVENTS.register(id,
                () -> SoundEvent.createFixedRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(TestMod.MODID, id),
                        range));
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}
