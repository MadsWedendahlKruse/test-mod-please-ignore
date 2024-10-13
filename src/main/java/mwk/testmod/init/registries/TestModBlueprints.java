package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = TestMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TestModBlueprints {

    private TestModBlueprints() {
    }

    public static final ResourceKey<Registry<MultiBlockBlueprint>> BLUEPRINT_REGISTRY_KEY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "blueprints"));

    public static final ResourceKey<MultiBlockBlueprint> CRUSHER_KEY = ResourceKey
            .create(BLUEPRINT_REGISTRY_KEY,
                    ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "crusher"));
    public static final ResourceKey<MultiBlockBlueprint> INDUCTION_FURNACE_KEY = ResourceKey.create(
            BLUEPRINT_REGISTRY_KEY,
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "induction_furnace"));
    public static final ResourceKey<MultiBlockBlueprint> SUPER_ASSEMBLER_KEY = ResourceKey
            .create(BLUEPRINT_REGISTRY_KEY,
                    ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "super_assembler"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BLUEPRINT_REGISTRY_KEY, MultiBlockBlueprint.CODEC,
                MultiBlockBlueprint.CODEC);
    }
}
