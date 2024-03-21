package mwk.testmod.client.render.hologram.components;

import java.util.Collection;
import javax.annotation.Nullable;
import org.joml.Quaternionf;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintBlockInfo;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintState;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModItems;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HologramItemRenderer {

    private static final float HOLOGRAM_ITEM_SCALE = 0.15F;
    private static final float HOLOGRAM_ITEM_ALPHA = 1.0F;
    private static final float HOLOGRAM_ITEM_SPACING = 0.05F;
    private static final float LIST_ELEMENT_HEIGHT = HOLOGRAM_ITEM_SCALE + HOLOGRAM_ITEM_SPACING;

    private static final float[] HOLOGRAM_COLOR_WHITE = new float[] {1, 1, 1};

    private ItemRenderer itemRenderer;
    private BufferSource bufferSource;

    public HologramItemRenderer(ItemRenderer itemRenderer, BufferSource bufferSource) {
        this.itemRenderer = itemRenderer;
        this.bufferSource = bufferSource;
    }

    public enum ItemStackTextRenderType {
        NONE, COUNT, NAME, COUNT_AND_NAME
    }

    private void renderItemCount(PoseStack poseStack, ItemStack itemStack,
            HologramTextRenderer textRenderer) {
        textRenderer.renderText(poseStack, Component.literal(itemStack.getCount() + ""),
                -HOLOGRAM_ITEM_SCALE / 8, -HOLOGRAM_ITEM_SCALE / 8,
                // Tiny offset to avoid z-fighting
                -(HOLOGRAM_ITEM_SCALE / 2 + 0.01F));
    }

    private void renderItemName(PoseStack poseStack, ItemStack itemStack,
            HologramTextRenderer textRenderer, int textColor) {
        textRenderer.renderText(poseStack, itemStack.getHoverName(), -HOLOGRAM_ITEM_SCALE,
                HOLOGRAM_ITEM_SCALE / 4, 0.0F, textColor);
    }

    private void renderItemName(PoseStack poseStack, ItemStack itemStack,
            HologramTextRenderer textRenderer) {
        renderItemName(poseStack, itemStack, textRenderer, HologramTextRenderer.TEXT_WHITE);
    }

    public void renderItemStack(PoseStack poseStack, ItemStack itemStack, float x, float y, float z,
            float[] color, float alpha, float scale, ItemDisplayContext context,
            @Nullable HologramTextRenderer textRenderer, ItemStackTextRenderType textRenderType,
            int textColor, boolean endBatch) {
        if (color != null) {
            RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);
        }
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(scale, scale, scale);
        itemRenderer.renderStatic(itemStack, context, LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, null, 0);
        if (endBatch) {
            bufferSource.endBatch();
        }
        if (textRenderer != null) {
            // TODO: This is a bit of a hack to undo the scaling of the item stack
            poseStack.scale(1.0F / scale, 1.0F / scale, 1.0F / scale);
            switch (textRenderType) {
                case COUNT:
                    renderItemCount(poseStack, itemStack, textRenderer);
                    break;
                case NAME:
                    renderItemName(poseStack, itemStack, textRenderer, textColor);
                    break;
                case COUNT_AND_NAME:
                    renderItemCount(poseStack, itemStack, textRenderer);
                    renderItemName(poseStack, itemStack, textRenderer, textColor);
                    break;
                case NONE:
                default:
                    break;
            }
        }
        poseStack.popPose();
    }

    public void renderItemStack(PoseStack poseStack, ItemStack itemStack, float x, float y, float z,
            float[] itemColor, @Nullable HologramTextRenderer textRenderer,
            ItemStackTextRenderType textRenderType, int textColor, boolean endBatch) {
        renderItemStack(poseStack, itemStack, x, y, z, itemColor, HOLOGRAM_ITEM_ALPHA,
                HOLOGRAM_ITEM_SCALE, ItemDisplayContext.NONE, textRenderer, textRenderType,
                textColor, endBatch);
    }

    public float renderItemStackList(PoseStack poseStack, Collection<ItemStack> itemStacks, float x,
            float y, float z, float[] itemColor, @Nullable HologramTextRenderer textRenderer,
            ItemStackTextRenderType textRenderType, int textColor, Component header,
            boolean endBatch) {
        float height = 0.0F;
        if (textRenderer != null && header != null) {
            textRenderer.renderText(poseStack, header, x + HOLOGRAM_ITEM_SCALE / 2, y, z,
                    textColor);
            height -= LIST_ELEMENT_HEIGHT;
        }
        for (ItemStack itemStack : itemStacks) {
            renderItemStack(poseStack, itemStack, x, y + height, z, itemColor, textRenderer,
                    textRenderType, textColor, endBatch);
            height -= LIST_ELEMENT_HEIGHT;
        }
        if (endBatch) {
            bufferSource.endBatch();
        }
        return height;
    }

    public float renderItemStackList(PoseStack poseStack, Collection<ItemStack> itemStacks, float x,
            float y, float z, float[] itemColor, @Nullable HologramTextRenderer textRenderer,
            ItemStackTextRenderType textRenderType, int textColor, boolean endBatch) {
        return renderItemStackList(poseStack, itemStacks, x, y, z, itemColor, textRenderer,
                textRenderType, textColor, null, endBatch);
    }

    public void renderBlueprintStatus(PoseStack poseStack, HologramTextRenderer textRenderer,
            BlueprintState blueprintState) {
        float height = 0;
        poseStack.pushPose();
        poseStack.translate(-HOLOGRAM_ITEM_SCALE / 2, 0, -HOLOGRAM_ITEM_SCALE);
        if (!blueprintState.isComplete()) {
            Collection<ItemStack> missingBlockStacks =
                    BlueprintBlockInfo.getItemStacks(blueprintState.getMissingBlocks(), false);
            Collection<ItemStack> incorrectBlockStacks =
                    BlueprintBlockInfo.getItemStacks(blueprintState.getIncorrectBlocks(), true);
            // Estimate the height of the text to render the missing and incorrect blocks so they
            // don't go into the ground (+1 for the header text)
            height += Math.max((missingBlockStacks.size() + incorrectBlockStacks.size() + 1)
                    * LIST_ELEMENT_HEIGHT - 1, 0);
            if (!missingBlockStacks.isEmpty()) {
                height += renderItemStackList(poseStack, missingBlockStacks, 0, height, 0,
                        HOLOGRAM_COLOR_WHITE, textRenderer, ItemStackTextRenderType.COUNT_AND_NAME,
                        HologramTextRenderer.TEXT_WHITE, Component.translatable(
                                TestModLanguageProvider.KEY_INFO_CONTROLLER_MISSING_BLOCKS),
                        true);
            }
            if (!incorrectBlockStacks.isEmpty()) {
                height += renderItemStackList(poseStack, incorrectBlockStacks, 0, height, 0,
                        HOLOGRAM_COLOR_WHITE, textRenderer, ItemStackTextRenderType.COUNT_AND_NAME,
                        HologramTextRenderer.TEXT_RED,
                        Component.translatable(
                                TestModLanguageProvider.KEY_INFO_CONTROLLER_INCORRECT_BLOCKS),
                        true);
            }
        } else {
            textRenderer.renderTextList(poseStack, new Component[] {
                    Component.translatable(
                            TestModLanguageProvider.KEY_INFO_CONTROLLER_HOLOGRAM_COMPLETE),
                    Component.translatable(
                            TestModLanguageProvider.KEY_INFO_CONTROLLER_HOLOGRAM_RIGHT_CLICK_ME)},
                    0, height, 0, LIST_ELEMENT_HEIGHT);
            // Delay popping until after all text is rendered
            poseStack.popPose();
            // End batch here since we're changing the shader color when rendering the wrench
            bufferSource.endBatch();
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotationY((float) Math.PI));
            // Since we've rotated around the y-axis, the translations along the x and z axes are
            // now POSITIVE instead of the usual NEGATIVE. This is a bit annoying, and I'm not sure
            // if it's worth the hassle. On the other hand it works *shrug*
            renderItemStack(poseStack, new ItemStack(TestModItems.WRENCH_ITEM.get()), 0.5F, -0.65F,
                    0.25F, HOLOGRAM_COLOR_WHITE, 1.0F, 0.5F, ItemDisplayContext.GUI, null,
                    ItemStackTextRenderType.NONE, HologramTextRenderer.TEXT_WHITE, true);
        }
        poseStack.popPose();
    }
}
