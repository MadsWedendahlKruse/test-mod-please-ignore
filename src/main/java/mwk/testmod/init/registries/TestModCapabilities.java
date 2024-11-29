package mwk.testmod.init.registries;

import mwk.testmod.TestMod;
import mwk.testmod.common.capabilities.ITemporalFluxHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class TestModCapabilities {

    private TestModCapabilities() {
    }

    public static final class TemporalFluxHandler {

        public static final BlockCapability<ITemporalFluxHandler, @Nullable Direction> BLOCK =
                BlockCapability.createSided(
                        ResourceLocation.fromNamespaceAndPath(TestMod.MODID,
                                "temporal_flux_handler"),
                        ITemporalFluxHandler.class);

        private TemporalFluxHandler() {
        }
    }


}
