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
						// Blocks
						output.accept(TestModBlocks.MACHINE_FRAME_BASIC);
						output.accept(TestModBlocks.MACHINE_FRAME_REINFORCED);
						output.accept(TestModBlocks.MACHINE_FRAME_ADVANCED);
						output.accept(TestModBlocks.MACHINE_INPUT_PORT);
						output.accept(TestModBlocks.MACHINE_OUTPUT_PORT);
						output.accept(TestModBlocks.MACHINE_ENERGY_PORT);

						output.accept(TestModBlocks.COPPER_COIL);

						output.accept(TestModBlocks.INDUCTION_FURNACE);
						output.accept(TestModBlocks.SUPER_ASSEMBLER);
						output.accept(TestModBlocks.CRUSHER);

						// Items
						output.accept(TestModItems.WRENCH_ITEM);
						output.accept(TestModItems.HOLOGRAM_PROJECTOR_ITEM);

						output.accept(TestModItems.COAL_DUST);
						output.accept(TestModItems.IRON_DUST);
						output.accept(TestModItems.STEEL_DUST);
						output.accept(TestModItems.STEEL_INGOT);
					}).build());

	public static void register(IEventBus modEventBus) {
		CREATIVE_MODE_TABS.register(modEventBus);
	}
}
