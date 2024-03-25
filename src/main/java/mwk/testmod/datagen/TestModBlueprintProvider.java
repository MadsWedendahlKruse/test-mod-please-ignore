package mwk.testmod.datagen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public class TestModBlueprintProvider extends DatapackBuiltinEntriesProvider {

    public TestModBlueprintProvider(PackOutput output, CompletableFuture<Provider> registries) {
        /**
         * TODO: This doesn't work, probably because the getters in the MultiBlockBlueprint codec
         * are null? Not super high priority, but it would be nice to have this working.
         */
        super(output, registries, new RegistrySetBuilder()
                .add(TestModBlueprints.BLUEPRINT_REGISTRY_KEY, bootstrap -> {
                    List<List<String>> layers = new ArrayList<>();
                    layers.add(List.of("rar", "OaO", "rEr"));
                    layers.add(List.of("bXb", "bab", "bbb"));
                    layers.add(List.of("b b", " a ", "b b"));
                    layers.add(List.of("rrr", "IrI", "rrr"));
                    Map<String, String> blockMap = new HashMap<>();
                    blockMap.put("X", "testmod:crusher");
                    blockMap.put("I", "testmod:machine_input_port");
                    blockMap.put("O", "testmod:machine_output_port");
                    blockMap.put("E", "testmod:machine_energy_port");
                    blockMap.put("b", "testmod:machine_frame_basic");
                    blockMap.put("r", "testmod:machine_frame_reinforced");
                    blockMap.put("a", "testmod:machine_frame_advanced");
                    MultiBlockBlueprint blueprint = new MultiBlockBlueprint(layers, blockMap);
                    bootstrap.register(TestModBlueprints.CRUSHER_KEY, blueprint);
                }), Set.of(TestMod.MODID));
    }
}
