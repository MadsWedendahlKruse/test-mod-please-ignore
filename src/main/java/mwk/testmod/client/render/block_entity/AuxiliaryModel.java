package mwk.testmod.client.render.block_entity;

import mwk.testmod.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Class for any auxiliary models that are used in block entity renderers, e.g. rotors in a crusher.
 */
public class AuxiliaryModel {

    private final String modelPath;
    private BakedModel bakedModel = null;

    public AuxiliaryModel(String modelPath) {
        this.modelPath = modelPath;
    }

    public BakedModel getBakedModel() {
        if (bakedModel == null) {
            bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
                    .getModelManager().getModel(new ResourceLocation(TestMod.MODID, modelPath));
        }
        return bakedModel;
    }
}
