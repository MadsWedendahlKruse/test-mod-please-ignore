package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.client.render.models.AuxiliaryModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

public class TestModModels {

    private TestModModels() {}

    public static final String CRUSHER_ROTOR_PATH = "multiblock/crusher_rotor";
    public static final String SEPATATOR_SPINNER_PATH = "multiblock/separator_spinner";
    public static final String SEPATATOR_WIREFRAME_PATH = "multiblock/separator_wireframe";

    public static final AuxiliaryModel CRUSHER_ROTOR = new AuxiliaryModel(CRUSHER_ROTOR_PATH);
    public static final AuxiliaryModel SEPARATOR_SPINNER =
            new AuxiliaryModel(SEPATATOR_SPINNER_PATH);
    public static final AuxiliaryModel SEPARATOR_WIREFRAME =
            new AuxiliaryModel(SEPATATOR_WIREFRAME_PATH);

    public static void register(ModelEvent.RegisterAdditional event) {
        event.register(new ResourceLocation(TestMod.MODID, CRUSHER_ROTOR_PATH));
        event.register(new ResourceLocation(TestMod.MODID, SEPATATOR_SPINNER_PATH));
        event.register(new ResourceLocation(TestMod.MODID, SEPATATOR_WIREFRAME_PATH));
    }
}
