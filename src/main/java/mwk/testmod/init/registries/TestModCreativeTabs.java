package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModCreativeTabs {

	private TestModCreativeTabs() {}

	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TestMod.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB =
			CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup." + TestMod.MODID + ".example_tab"))
					.withTabsBefore(CreativeModeTabs.COMBAT)
					.icon(() -> TestModItems.WRENCH_ITEM.get().getDefaultInstance())
					.displayItems((parameters, output) -> {
						output.accept(TestModBlocks.MACHINE_FRAME_BASIC_BLOCK);
						output.accept(TestModBlocks.MACHINE_FRAME_REINFORCED_BLOCK);
						output.accept(TestModBlocks.MACHINE_FRAME_ADVANCED_BLOCK);
						output.accept(TestModBlocks.MACHINE_INPUT_PORT_BLOCK);
						output.accept(TestModBlocks.MACHINE_OUTPUT_PORT_BLOCK);
						output.accept(TestModBlocks.MACHINE_ENERGY_PORT_BLOCK);

						output.accept(TestModBlocks.SUPER_FURNACE_BLOCK);
						output.accept(TestModBlocks.SUPER_ASSEMBLER_BLOCK);
						output.accept(TestModBlocks.CRUSHER_BLOCK);

						output.accept(TestModItems.WRENCH_ITEM);
						output.accept(TestModItems.HOLOGRAM_PROJECTOR_ITEM);
					}).build());

	public static void register(IEventBus modEventBus) {
		CREATIVE_MODE_TABS.register(modEventBus);
	}
}
