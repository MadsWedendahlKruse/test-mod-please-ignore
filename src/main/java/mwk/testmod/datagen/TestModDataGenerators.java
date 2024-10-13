package mwk.testmod.datagen;

import java.util.concurrent.CompletableFuture;
import mwk.testmod.TestMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = TestMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TestModDataGenerators {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(),
                new TestModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(),
                new TestModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(),
                TestModLootTableProvider.create(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(),
                new TestModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(),
                new TestModLanguageProvider(packOutput, "en_us"));
        TestModBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(),
                new TestModBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new TestModItemTagProvider(packOutput,
                lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));
        // TODO: Implement TestModBlueprintProvider
        // generator.addProvider(event.includeServer(),
        // new TestModBlueprintProvider(packOutput, lookupProvider));
    }
}
