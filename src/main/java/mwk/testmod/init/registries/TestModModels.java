package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.client.render.models.AuxiliaryModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

public class TestModModels {

    private TestModModels() {
    }

    public static final String CRUSHER_ROTOR_PATH = "multiblock/crusher_rotor";
    public static final String SEPATATOR_SPINNER_PATH = "multiblock/separator_spinner";
    public static final String STIRLING_GENERATOR_FLYWHEEL_PATH =
            "multiblock/stirling_generator_flywheel";
    public static final String STIRLING_GENERATOR_PISTON_PATH =
            "multiblock/stirling_generator_piston";
    public static final String STAMPING_PRESS_PISTON_PATH = "multiblock/stamping_press_piston";

    public static final AuxiliaryModel CRUSHER_ROTOR = new AuxiliaryModel(CRUSHER_ROTOR_PATH);
    public static final AuxiliaryModel SEPARATOR_SPINNER =
            new AuxiliaryModel(SEPATATOR_SPINNER_PATH);
    public static final AuxiliaryModel STIRLING_GENERATOR_FLYWHEEL =
            new AuxiliaryModel(STIRLING_GENERATOR_FLYWHEEL_PATH);
    public static final AuxiliaryModel STIRLING_GENERATOR_PISTON =
            new AuxiliaryModel(STIRLING_GENERATOR_PISTON_PATH);
    public static final AuxiliaryModel STAMPING_PRESS_PISTON =
            new AuxiliaryModel(STAMPING_PRESS_PISTON_PATH);

    public static void register(ModelEvent.RegisterAdditional event) {
        event.register(
                new ModelResourceLocation(
                        ResourceLocation.fromNamespaceAndPath(TestMod.MODID, CRUSHER_ROTOR_PATH),
                        ""));
        event.register(
                new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(TestMod.MODID,
                        SEPATATOR_SPINNER_PATH), ""));
        event.register(
                new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(TestMod.MODID,
                        STIRLING_GENERATOR_FLYWHEEL_PATH), ""));
        event.register(
                new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(TestMod.MODID,
                        STIRLING_GENERATOR_PISTON_PATH), ""));
        event.register(
                new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(TestMod.MODID,
                        STAMPING_PRESS_PISTON_PATH), ""));
    }
}
