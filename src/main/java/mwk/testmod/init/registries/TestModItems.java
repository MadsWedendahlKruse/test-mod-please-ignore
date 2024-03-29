package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.item.tools.HologramProjectorItem;
import mwk.testmod.common.item.tools.WrenchItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModItems {

	private TestModItems() {}

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TestMod.MODID);

	public static final DeferredItem<WrenchItem> WRENCH_ITEM =
			ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties()));
	public static final DeferredItem<HologramProjectorItem> HOLOGRAM_PROJECTOR_ITEM = ITEMS
			.register("hologram_projector", () -> new HologramProjectorItem(new Item.Properties()));

	public static final DeferredItem<Item> COAL_DUST =
			ITEMS.register("dust_coal", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> IRON_DUST =
			ITEMS.register("dust_iron", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> STEEL_DUST =
			ITEMS.register("dust_steel", () -> new Item(new Item.Properties()));
	public static final DeferredItem<Item> STEEL_INGOT =
			ITEMS.register("ingot_steel", () -> new Item(new Item.Properties()));

	public static void register(IEventBus modEventBus) {
		ITEMS.register(modEventBus);
	}
}

