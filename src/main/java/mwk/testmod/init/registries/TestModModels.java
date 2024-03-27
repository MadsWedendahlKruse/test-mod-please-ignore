package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.client.render.block_entity.AuxillaryModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

public class TestModModels {

    private TestModModels() {}

    public static final String CRUSHER_ROTOR_PATH = "multiblock/crusher_rotor";

    public static final AuxillaryModel CRUSHER_ROTOR = new AuxillaryModel(CRUSHER_ROTOR_PATH);

    public static void register(ModelEvent.RegisterAdditional event) {
        event.register(new ResourceLocation(TestMod.MODID, CRUSHER_ROTOR_PATH));
    }
}
