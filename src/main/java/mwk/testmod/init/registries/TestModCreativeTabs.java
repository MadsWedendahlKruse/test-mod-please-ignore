package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.HologramBlock;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModCreativeTabs {

	private TestModCreativeTabs() {}

	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TestMod.MODID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB =
			CREATIVE_MODE_TABS.register(TestMod.MODID,
					() -> CreativeModeTab.builder()
							.title(Component.translatable(TestModLanguageProvider.KEY_CREATIVE_TAB))
							.icon(() -> TestModItems.WRENCH_ITEM.get().getDefaultInstance())
							.displayItems((parameters, output) -> {
								// Items
								TestModItems.ITEMS.getEntries().forEach(entry -> {
									output.accept(entry.get());
								});
								// Blocks
								TestModBlocks.BLOCKS.getEntries().forEach(entry -> {
									// Hacky way to exclude hologram blocks from the creative tab
									if (!(entry.get() instanceof HologramBlock)) {
										output.accept(entry.get());
									}
								});
							}).build());

	public static void addToTabs(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
			// TODO: Not sure what to do with the multiblock parts
		}
		if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			event.accept(TestModBlocks.ILMENITE_ORE.get());
			event.accept(TestModBlocks.DEEPSLATE_ILMENITE_ORE.get());
		}
		if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			// TODO: Not sure what to do with the multiblock parts
			// They don't really have any functionality on their own
			event.accept(TestModBlocks.MACHINE_FRAME_BASIC.get());
			event.accept(TestModBlocks.MACHINE_FRAME_REINFORCED.get());
			event.accept(TestModBlocks.MACHINE_FRAME_ADVANCED.get());
			event.accept(TestModBlocks.MACHINE_ITEM_INPUT_PORT.get());
			event.accept(TestModBlocks.MACHINE_ITEM_OUTPUT_PORT.get());
			event.accept(TestModBlocks.MACHINE_ENERGY_PORT.get());
			event.accept(TestModBlocks.COPPER_COIL.get());
			event.accept(TestModBlocks.INDUCTION_FURNACE.get());
			event.accept(TestModBlocks.SUPER_ASSEMBLER.get());
			event.accept(TestModBlocks.CRUSHER.get());
			event.accept(TestModBlocks.SEPARATOR.get());
		}
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(TestModItems.WRENCH_ITEM.get());
			event.accept(TestModItems.HOLOGRAM_PROJECTOR_ITEM.get());
		}
		if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			event.accept(TestModItems.RAW_ILMENITE.get());
			event.accept(TestModItems.COAL_DUST.get());
			event.accept(TestModItems.IRON_DUST.get());
			event.accept(TestModItems.STEEL_DUST.get());
			event.accept(TestModItems.ILMENITE_DUST.get());
			event.accept(TestModItems.TITANIUM_DUST.get());
			event.accept(TestModItems.STEEL_INGOT.get());
			event.accept(TestModItems.TITANIUM_INGOT.get());
		}
	}

	public static void register(IEventBus modEventBus) {
		CREATIVE_MODE_TABS.register(modEventBus);
	}
}
