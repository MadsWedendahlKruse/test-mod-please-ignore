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
            CREATIVE_MODE_TABS.register("example_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.testmod"))
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(() -> TestModItems.WRENCH_ITEM.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(TestModItems.MACHINE_FRAME_BASIC_BLOCK_ITEM.get());
                                output.accept(
                                        TestModItems.MACHINE_FRAME_REINFORCED_BLOCK_ITEM.get());
                                output.accept(TestModItems.MACHINE_FRAME_ADVANCED_BLOCK_ITEM.get());
                                output.accept(TestModItems.SUPER_FURNACE_BLOCK_ITEM.get());
                                output.accept(TestModItems.SUPER_ASSEMBLER_BLOCK_ITEM.get());
                                output.accept(TestModItems.WRENCH_ITEM.get());
                            }).build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
