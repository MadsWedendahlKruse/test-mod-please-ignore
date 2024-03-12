package mwk.testmod.init.registries;

import java.util.function.Supplier;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.SuperFurnaceMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModMenus {

    private TestModMenus() {}

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, TestMod.MODID);

    // public static final DeferredHolder<MenuType<?>, MenuType<SuperFurnaceMenu>>
    // SUPER_FURNACE_MENU =
    // MENUS.register("super_furnace",
    // () -> new MenuType<SuperFurnaceMenu>(SuperFurnaceMenu::new,
    // FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<SuperFurnaceMenu>> SUPER_FURNACE_MENU =
            MENUS.register("super_furnace", () -> IMenuTypeExtension.create((windowId, inv,
                    data) -> new SuperFurnaceMenu(windowId, inv.player, data.readBlockPos())));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
