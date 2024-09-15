package mwk.testmod.init.registries;

import java.util.function.Supplier;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.inventory.CapacitronMenu;
import mwk.testmod.common.block.inventory.CrusherMenu;
import mwk.testmod.common.block.inventory.GeothermalGeneratorMenu;
import mwk.testmod.common.block.inventory.InductionFurnaceMenu;
import mwk.testmod.common.block.inventory.RedstoneGeneratorMenu;
import mwk.testmod.common.block.inventory.SeparatorMenu;
import mwk.testmod.common.block.inventory.StampingPressMenu;
import mwk.testmod.common.block.inventory.StirlingGeneratorMenu;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TestModMenus {

    private TestModMenus() {
    }

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, TestMod.MODID);

    public static final Supplier<MenuType<InductionFurnaceMenu>> INDUCTION_FURNACE_MENU = registerMultiBlockControllerMenu(
            TestModBlocks.INDUCTION_FURNACE, InductionFurnaceMenu::new);
    public static final Supplier<MenuType<CrusherMenu>> CRUSHER_MENU = registerMultiBlockControllerMenu(
            TestModBlocks.CRUSHER, CrusherMenu::new);
    public static final Supplier<MenuType<SeparatorMenu>> SEPARATOR_MENU = registerMultiBlockControllerMenu(
            TestModBlocks.SEPARATOR, SeparatorMenu::new);
    public static final Supplier<MenuType<StampingPressMenu>> STAMPING_PRESS_MENU = registerMultiBlockControllerMenu(
            TestModBlocks.STAMPING_PRESS, StampingPressMenu::new);
    public static final Supplier<MenuType<RedstoneGeneratorMenu>> REDSTONE_GENERATOR_MENU = registerMultiBlockControllerMenu(
            TestModBlocks.REDSTONE_GENERATOR, RedstoneGeneratorMenu::new);
    public static final Supplier<MenuType<GeothermalGeneratorMenu>> GEOTHERMAL_GENERATOR_MENU = registerMultiBlockControllerMenu(
            TestModBlocks.GEOTHERMAL_GENERATOR, GeothermalGeneratorMenu::new);
    public static final Supplier<MenuType<StirlingGeneratorMenu>> STIRLING_GENERATOR_MENU = registerMultiBlockControllerMenu(
            TestModBlocks.STIRLING_GENERATOR, StirlingGeneratorMenu::new);
    public static final Supplier<MenuType<CapacitronMenu>> CAPACITRON_MENU = registerMultiBlockControllerMenu(
            TestModBlocks.CAPACITRON, CapacitronMenu::new);

    @FunctionalInterface
    public interface MultiBlockControllerMenuFactory<T extends AbstractContainerMenu> {

        T create(int windowId, Player player, BlockPos pos);
    }

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMultiBlockControllerMenu(
            DeferredBlock<? extends MultiBlockControllerBlock> block,
            MultiBlockControllerMenuFactory<T> factory) {
        return MENUS.register(block.getId().getPath(),
                () -> IMenuTypeExtension.create(
                        (windowId, inv, data) -> factory.create(windowId,
                                inv.player, data.readBlockPos())));
    }

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
