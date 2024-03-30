package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.HologramBlock;
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

	public static void register(IEventBus modEventBus) {
		CREATIVE_MODE_TABS.register(modEventBus);
	}
}
