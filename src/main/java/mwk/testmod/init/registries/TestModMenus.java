package mwk.testmod.init.registries;

import java.util.function.Supplier;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.CrusherMenu;
import mwk.testmod.common.block.inventory.InductionFurnaceMenu;
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
                        MENUS.register("induction_furnace", () -> IMenuTypeExtension.create(
                                        (windowId, inv, data) -> new InductionFurnaceMenu(windowId,
                                                        inv.player, data.readBlockPos())));

        public static final Supplier<MenuType<CrusherMenu>> CRUSHER_MENU = MENUS.register("crusher",
                        () -> IMenuTypeExtension
                                        .create((windowId, inv, data) -> new CrusherMenu(windowId,
                                                        inv.player, data.readBlockPos())));

        public static void register(IEventBus modEventBus) {
                MENUS.register(modEventBus);
        }
}
