package mwk.testmod.init.registries;

import java.util.function.Supplier;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.CrusherMenu;
import mwk.testmod.common.block.inventory.GeothermalGeneratorMenu;
import mwk.testmod.common.block.inventory.InductionFurnaceMenu;
import mwk.testmod.common.block.inventory.RedstoneGeneratorMenu;
import mwk.testmod.common.block.inventory.SeparatorMenu;
import mwk.testmod.common.block.inventory.StirlingGeneratorMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModMenus {

	private TestModMenus() {}

	public static final DeferredRegister<MenuType<?>> MENUS =
			DeferredRegister.create(BuiltInRegistries.MENU, TestMod.MODID);

	public static final Supplier<MenuType<InductionFurnaceMenu>> INDUCTION_FURNACE_MENU =
			MENUS.register(TestModBlocks.INDUCTION_FURNACE_ID,
					() -> IMenuTypeExtension
							.create((windowId, inv, data) -> new InductionFurnaceMenu(windowId,
									inv.player, data.readBlockPos())));

	public static final Supplier<MenuType<CrusherMenu>> CRUSHER_MENU =
			MENUS.register(TestModBlocks.CRUSHER_ID, () -> IMenuTypeExtension.create((windowId, inv,
					data) -> new CrusherMenu(windowId, inv.player, data.readBlockPos())));

	public static final Supplier<MenuType<SeparatorMenu>> SEPARATOR_MENU =
			MENUS.register(TestModBlocks.SEPARATOR_ID, () -> IMenuTypeExtension.create((windowId,
					inv, data) -> new SeparatorMenu(windowId, inv.player, data.readBlockPos())));

	public static final Supplier<MenuType<RedstoneGeneratorMenu>> REDSTONE_GENERATOR_MENU =
			MENUS.register(TestModBlocks.REDSTONE_GENERATOR_ID,
					() -> IMenuTypeExtension
							.create((windowId, inv, data) -> new RedstoneGeneratorMenu(windowId,
									inv.player, data.readBlockPos())));

	public static final Supplier<MenuType<GeothermalGeneratorMenu>> GEOTHERMAL_GENERATOR_MENU =
			MENUS.register(TestModBlocks.GEOTHERMAL_GENERATOR_ID,
					() -> IMenuTypeExtension
							.create((windowId, inv, data) -> new GeothermalGeneratorMenu(windowId,
									inv.player, data.readBlockPos())));

	public static final Supplier<MenuType<StirlingGeneratorMenu>> STIRLING_GENERATOR_MENU =
			MENUS.register(TestModBlocks.STIRLING_GENERATOR_ID,
					() -> IMenuTypeExtension
							.create((windowId, inv, data) -> new StirlingGeneratorMenu(windowId,
									inv.player, data.readBlockPos())));

	public static void register(IEventBus modEventBus) {
		MENUS.register(modEventBus);
	}
}
