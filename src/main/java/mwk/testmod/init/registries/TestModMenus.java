package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModMenus {

    private TestModMenus() {}

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TestMod.MODID);

    // public static final DeferredHolder<MenuType<?>, MenuType<?>> HOLOGRAM_PROJECTOR_MENU =
    // MENUS.register("hologram_projector", () -> new MenuType());

}
