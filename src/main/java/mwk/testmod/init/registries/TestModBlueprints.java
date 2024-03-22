package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = TestMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TestModBlueprints {

    private TestModBlueprints() {}

    public static final ResourceKey<Registry<MultiBlockBlueprint>> BLUEPRINT_REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation(TestMod.MODID, "blueprints"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BLUEPRINT_REGISTRY_KEY, MultiBlockBlueprint.CODEC,
                MultiBlockBlueprint.CODEC);
    }
}
