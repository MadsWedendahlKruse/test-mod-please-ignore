package mwk.testmod.client.render.models;

import java.util.List;
import mwk.testmod.TestMod;
import mwk.testmod.client.render.RenderUtils;
import mwk.testmod.client.render.RenderUtils.UnpackedQuad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.Lazy;

/**
 * Class for any auxiliary models that are used in block entity renderers, e.g. rotors in a crusher.
 */
public class AuxiliaryModel {

    private final String modelPath;
    private final Lazy<BakedModel> bakedModel;
    private final Lazy<List<UnpackedQuad>> quads;

    public AuxiliaryModel(String modelPath) {
        this.modelPath = modelPath;
        this.bakedModel = Lazy.of(() -> Minecraft.getInstance().getBlockRenderer()
                .getBlockModelShaper().getModelManager()
                .getModel(new ResourceLocation(TestMod.MODID, modelPath)));
        this.quads = Lazy.of(() -> RenderUtils.unpackQuads(bakedModel.get()));
    }

    public BakedModel getBakedModel() {
        return bakedModel.get();
    }

    public List<UnpackedQuad> getQuads() {
        return quads.get();
    }
}
