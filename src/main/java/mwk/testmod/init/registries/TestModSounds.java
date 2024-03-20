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

	public static final String MULTIBLOCK_FORM_ID = "block.multiblock.form";
	public static final String MULTIBLOCK_CRUSHER_ID = "block.multiblock.crusher";
	public static final int MULTIBLOCK_CRUSHER_DURATION = 20; // [ticks]
	public static final int MULTIBLOCK_CRUSHER_RANGE = 32; // [blocks]

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
			DeferredRegister.create(Registries.SOUND_EVENT, TestMod.MODID);

	public static final DeferredHolder<SoundEvent, SoundEvent> MULTIBLOCK_FORM =
			SOUND_EVENTS.register(MULTIBLOCK_FORM_ID, () -> SoundEvent.createVariableRangeEvent(
					new ResourceLocation(TestMod.MODID, MULTIBLOCK_FORM_ID)));
	public static final DeferredHolder<SoundEvent, SoundEvent> MULTIBLOCK_CRUSHER =
			SOUND_EVENTS.register(MULTIBLOCK_CRUSHER_ID,
					() -> SoundEvent.createFixedRangeEvent(
							new ResourceLocation(TestMod.MODID, MULTIBLOCK_CRUSHER_ID),
							MULTIBLOCK_CRUSHER_RANGE));

	public static void register(IEventBus modEventBus) {
		SOUND_EVENTS.register(modEventBus);
	}
}
