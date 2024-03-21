package mwk.testmod.client.gui.screen;

import java.util.ArrayList;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import mwk.testmod.TestMod;
import mwk.testmod.client.animations.FixedAnimationFloat;
import mwk.testmod.client.animations.PerpetualAnimationFloat;
import mwk.testmod.client.gui.widgets.BlueprintList;
import mwk.testmod.client.gui.widgets.ButtonList;
import mwk.testmod.client.gui.widgets.OnOffButton;
import mwk.testmod.client.gui.widgets.ReleaseButton;
import mwk.testmod.client.render.block_entity.CrusherBlockEntityRenderer;
import mwk.testmod.common.block.entity.CrusherBlockEntity;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.MultiBlockPartBlock;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintBlockInfo;
import mwk.testmod.common.block.multiblock.blueprint.BlueprintRegistry;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.item.HologramProjectorItem;
import mwk.testmod.datagen.TestModLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HologramProjectorScreen extends Screen {

    // Rotating 3D model of the blueprint
    private static final int BLUEPRINT_MODEL_WIDTH = 130;
    private static final int BLUEPRINT_MODEL_HEIGHT = 130;
    private static final int BLUEPRINT_MODEL_PADDING = 5;
    private static final int BLUEPRINT_MODEL_Z = 150;
    private static final float BLUEPRINT_MODEL_SPIN_SPEED = 36; // [deg/s]
    private static final float BLUEPRINT_MODEL_MAX_LAYER_SPACING = 2.0F;

    // Buttons for manipulating the model
    private static final int BLUEPRINT_MODEL_BUTTON_SIZE = 20;
    private static final int BLUEPRINT_MODEL_BUTTON_SPACING = 2;

    // Materials for the blueprint
    private static final int BLUEPRINT_MATERIALS_WIDTH = BLUEPRINT_MODEL_WIDTH;
    private static final int BLUEPRINT_MATERIALS_ITEM_WIDTH = 20;
    private static final int BLUEPRINT_MATERIALS_ITEM_HEIGHT = 20;
    private static final int BLUEPRINT_MATERIALS_ITEMS_PER_ROW =
            BLUEPRINT_MATERIALS_WIDTH / BLUEPRINT_MATERIALS_ITEM_WIDTH;
    private static final int BLUEPRINT_MATERIALS_ITEM_ROWS = 2;
    private static final ResourceLocation EMPTY_BLOCK_SPRITE =
            new ResourceLocation(TestMod.MODID, "widget/empty_block");
    private static final int ITEMSTACK_PIXEL_SIZE = 16;

    // All three elements combined
    private static final int BLUEPRINT_WIDTH = BLUEPRINT_MODEL_WIDTH;
    private static final int BLUEPRINT_HEIGHT = BLUEPRINT_MODEL_HEIGHT + BLUEPRINT_MODEL_BUTTON_SIZE
            + BLUEPRINT_MATERIALS_ITEM_ROWS * BLUEPRINT_MATERIALS_ITEM_HEIGHT
            + (BLUEPRINT_MATERIALS_ITEM_ROWS + 1) * BLUEPRINT_MODEL_BUTTON_SPACING;
    private static final int BLUEPRINT_X_OFFSET = 10;

    private static final int BLUEPRINT_LIST_WIDTH = BLUEPRINT_MODEL_WIDTH;
    private static final int BLUEPRINT_LIST_HEIGHT = BLUEPRINT_HEIGHT;

    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation(TestMod.MODID, "textures/gui/hologram_projector.png");
    // TODO: Is it illegal to use something other than 256x256?
    private static final int BACKGROUND_TEXTURE_WIDTH = 512;
    private static final int BACKGROUND_TEXTURE_HEIGHT = 512;
    private static final int BACKGROUND_PADDING = 30;
    private static final int BACKGROUND_WIDTH = BLUEPRINT_WIDTH + BLUEPRINT_LIST_WIDTH
            + 2 * BLUEPRINT_X_OFFSET + 2 * BACKGROUND_PADDING;
    private static final int BACKGROUND_HEIGHT = BLUEPRINT_HEIGHT + 2 * BACKGROUND_PADDING;

    private BlueprintList blueprintList;
    private OnOffButton pauseAutoSpinButton;
    private OnOffButton counterClockwiseButton;
    private ReleaseButton manualSpinCounterClockwiseButton;
    private ReleaseButton manualSpinClockwiseButton;
    private OnOffButton splitButton;
    private OnOffButton formedButton;
    private ItemStack hologramProjector;

    private PerpetualAnimationFloat spinAnimation;
    private boolean manualSpinDirection;
    private FixedAnimationFloat mergeSplitAnimation;
    private float layerSpacing = 0.0F;

    public HologramProjectorScreen() {
        super(Component.literal("Placeholder"));
        spinAnimation = new PerpetualAnimationFloat(BLUEPRINT_MODEL_SPIN_SPEED);
        mergeSplitAnimation = new FixedAnimationFloat(0.5F,
                FixedAnimationFloat.Function.EASE_IN_CUBIC, 0.0F, 0.0F);
    }

    @Override
    protected void init() {
        super.init();

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            TestMod.LOGGER.error("Player is null");
            return;
        }
        this.hologramProjector = getHologramProjector(player);

        if (this.hologramProjector.isEmpty()) {
            // TODO: What to do here?
            TestMod.LOGGER.error("No hologram projector found in either hand");
            return;
        }
        this.blueprintList = new BlueprintList(this.width / 2 + BLUEPRINT_X_OFFSET,
                this.height / 2 - BLUEPRINT_LIST_HEIGHT / 2, BLUEPRINT_LIST_WIDTH,
                BLUEPRINT_LIST_HEIGHT, Component.translatable(
                        TestModLanguageProvider.KEY_WIDGET_HOLOGRAM_PROJECTOR_BLUEPRINTS));
        this.addRenderableWidget(this.blueprintList);

        ButtonList buttonList = new ButtonList(getBlueprintX(),
                getBlueprintY() + BLUEPRINT_MODEL_HEIGHT + BLUEPRINT_MODEL_BUTTON_SPACING,
                Component.translatable(
                        TestModLanguageProvider.KEY_WIDGET_HOLOGRAM_PROJECTOR_BUTTONS),
                true, BLUEPRINT_MODEL_BUTTON_SPACING);

        // Initialize all the buttons so they can reference eah other later
        pauseAutoSpinButton = new OnOffButton(BLUEPRINT_MODEL_BUTTON_SIZE, null, "play", "pause");
        counterClockwiseButton = new OnOffButton(BLUEPRINT_MODEL_BUTTON_SIZE, null,
                "swap_clockwise", "swap_counter_clockwise");
        manualSpinClockwiseButton = new ReleaseButton(BLUEPRINT_MODEL_BUTTON_SIZE, (pButton) -> {
            spinAnimation.resume();
            manualSpinDirection = false;
            pauseAutoSpinButton.setOn(true);
        }, (pButton) -> {
            spinAnimation.pause();
        }, "clockwise");
        manualSpinCounterClockwiseButton =
                new ReleaseButton(BLUEPRINT_MODEL_BUTTON_SIZE, (pButton) -> {
                    spinAnimation.resume();
                    manualSpinDirection = true;
                    pauseAutoSpinButton.setOn(true);
                }, (pButton) -> {
                    spinAnimation.pause();
                }, "counter_clockwise");
        splitButton = new OnOffButton(BLUEPRINT_MODEL_BUTTON_SIZE, null, "merge", "split");
        formedButton = new OnOffButton(BLUEPRINT_MODEL_BUTTON_SIZE, null, "unform", "form");

        // Buttons can now turn each other on or off when clicked
        pauseAutoSpinButton.setOnPressedExtra((button) -> {
            if (button.isOn()) {
                spinAnimation.pause();
            } else {
                spinAnimation.resume();
            }
        });
        splitButton.setOnPressedExtra((button) -> {
            mergeSplitAnimation.start();
            if (button.isOn()) {
                mergeSplitAnimation.setStartAndTarget(0.0F, BLUEPRINT_MODEL_MAX_LAYER_SPACING);
            } else {
                mergeSplitAnimation.setStartAndTarget(BLUEPRINT_MODEL_MAX_LAYER_SPACING, 0.0F);
            }
            formedButton.setOn(false);
        });
        formedButton.setOnPressedExtra((button) -> {
            splitButton.setOn(false);
        });

        buttonList.addButton(pauseAutoSpinButton);
        buttonList.addButton(counterClockwiseButton);
        buttonList.addButton(manualSpinClockwiseButton);
        buttonList.addButton(manualSpinCounterClockwiseButton);
        buttonList.addButton(splitButton);
        buttonList.addButton(formedButton);

        this.addRenderableWidget(buttonList);

        spinAnimation.start();
    }

    private ItemStack getHologramProjector(Player player) {
        // TODO: Can we just assume that one of the hands must hold the projector?
        if (player == null) {
            return ItemStack.EMPTY;
        }
        if (player.getMainHandItem().getItem() instanceof HologramProjectorItem) {
            return player.getMainHandItem();
        }
        if (player.getOffhandItem().getItem() instanceof HologramProjectorItem) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    private int getBlueprintX() {
        return this.width / 2 - BLUEPRINT_WIDTH - BLUEPRINT_X_OFFSET;
    }

    private int getBlueprintY() {
        return this.height / 2 - BLUEPRINT_HEIGHT / 2;
    }

    private int getBlueprintModelCenterX() {
        return getBlueprintX() + BLUEPRINT_MODEL_WIDTH / 2;
    }

    private int getBlueprintModelCenterY() {
        return getBlueprintY() + BLUEPRINT_MODEL_HEIGHT / 2;
    }


    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        // Check if any of the blueprint buttons are hovered
        blueprintList.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (blueprintList.buttonClicked(pMouseX, pMouseY, pButton)) {
            HologramProjectorItem.setBlueprintKey(hologramProjector,
                    blueprintList.getBlueprintKey());
            this.onClose();
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (Renderable widget : this.renderables) {
            if (widget instanceof AbstractWidget abstractWidget) {
                if (abstractWidget.isMouseOver(pMouseX, pMouseY)) {
                    abstractWidget.onRelease(pMouseX, pMouseY);
                    return true;
                }
            }
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY,
            float pPartialTick) {
        super.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blit(BACKGROUND_TEXTURE, this.width / 2 - BACKGROUND_WIDTH / 2,
                this.height / 2 - BACKGROUND_HEIGHT / 2, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                BACKGROUND_TEXTURE_WIDTH, BACKGROUND_TEXTURE_HEIGHT);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BufferSource bufferSource = guiGraphics.bufferSource();
        PoseStack poseStack = guiGraphics.pose();

        guiGraphics.blitSprite(BlueprintList.BACKGROUND_SPRITES.get(false, false), getBlueprintX(),
                getBlueprintY(), BLUEPRINT_MODEL_WIDTH, BLUEPRINT_MODEL_HEIGHT);

        MultiBlockBlueprint blueprint =
                BlueprintRegistry.getInstance().getBlueprint(blueprintList.getBlueprintKey());
        renderMaterials(
                guiGraphics, blueprint, getBlueprintX(), getBlueprintY() + BLUEPRINT_MODEL_HEIGHT
                        + BLUEPRINT_MODEL_BUTTON_SIZE + 2 * BLUEPRINT_MODEL_BUTTON_SPACING,
                mouseX, mouseY);
        if (blueprint == null) {
            return;
        }
        if (formedButton.isOn()) {
            renderMultiBlock(blueprint, poseStack, bufferSource, blockRenderer,
                    getBlueprintModelCenterX(), getBlueprintModelCenterY());
        } else {
            renderBlueprint(blueprint, poseStack, bufferSource, blockRenderer,
                    getBlueprintModelCenterX(), getBlueprintModelCenterY());
        }
    }

    private void renderBlueprint(MultiBlockBlueprint blueprint, PoseStack poseStack,
            BufferSource bufferSource, BlockRenderDispatcher blockRenderer, int x, int y) {
        setupPoseStack(blueprint, poseStack, x, y);
        Vec3 center = blueprint.getAABB().getCenter();
        BlueprintBlockInfo[] blocks = blueprint.getBlocks();
        // Calculate the sum of the y coordiantes of the "lowest" and "highest" blocks
        int ySum = blocks[0].getRelativePosition().getY()
                + blocks[blocks.length - 1].getRelativePosition().getY();
        if (ySum != 0) {
            // If it's non-zero that means the controller is not at the center of the blueprint
            // so we need to adjust the center
            center = center.add(0, (ySum) * layerSpacing / 2, 0);
        }
        renderBlocks(poseStack, bufferSource, blockRenderer, blocks, center, layerSpacing);
        bufferSource.endBatch();
        poseStack.popPose();
    }

    private void renderMultiBlock(MultiBlockBlueprint blueprint, PoseStack poseStack,
            BufferSource bufferSource, BlockRenderDispatcher blockRenderer, int x, int y) {
        setupPoseStack(blueprint, poseStack, x, y);
        MultiBlockControllerBlock controller = blueprint.getController();
        BlockState state =
                controller.defaultBlockState().setValue(MultiBlockPartBlock.FORMED, true);
        Vec3 center = blueprint.getAABB().getCenter();
        poseStack.translate(-center.x, -center.y, -center.z);
        blockRenderer.renderSingleBlock(state, poseStack, bufferSource, LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY);
        // Create a dummy block entity for the block entity renderer
        BlockEntity blockEntity = blueprint.getController().newBlockEntity(BlockPos.ZERO, state);
        BlockEntityRenderer<BlockEntity> blockEntityRenderer =
                Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        if (blockEntityRenderer != null) {
            blockEntityRenderer.render(blockEntity, 0, poseStack, bufferSource,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
        bufferSource.endBatch();
        poseStack.popPose();
    }

    private void setupPoseStack(MultiBlockBlueprint blueprint, PoseStack poseStack, int x, int y) {
        poseStack.pushPose();
        poseStack.translate(x, y, BLUEPRINT_MODEL_Z);

        boolean spinDirection = counterClockwiseButton.isOn();
        if (manualSpinClockwiseButton.isHeldDown()
                || manualSpinCounterClockwiseButton.isHeldDown()) {
            spinDirection = manualSpinDirection;
        }
        spinAnimation.update(spinDirection);
        // spinAnimation.update();
        float rotationAngle = spinAnimation.getValue();
        setupOrthographicProjection(poseStack, rotationAngle);

        if (!formedButton.isOn()) {
            mergeSplitAnimation.update();
            layerSpacing = mergeSplitAnimation.getValue();
        } else {
            layerSpacing = 0;
        }

        AABB aabb = blueprint.getAABB();
        double aabbYSize = aabb.getYsize();
        aabbYSize += Math.max(layerSpacing * aabb.getYsize() - 1, 0);
        final float diagonal = (float) Math.sqrt(aabb.getXsize() * aabb.getXsize()
                + aabbYSize * aabbYSize + aabb.getZsize() * aabb.getZsize());
        final float blueprintScale =
                (BLUEPRINT_MODEL_WIDTH - BLUEPRINT_MODEL_PADDING * 2) / diagonal;
        poseStack.scale(blueprintScale, blueprintScale, blueprintScale);
    }

    private void setupOrthographicProjection(PoseStack poseStack, float rotationAngle) {
        // TODO:
        // https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/main/src/main/java/appeng/integration/modules/itemlists/FluidBlockRendering.java
        // They can do it with one less rotation, but when I do the same the light is upside down

        poseStack.mulPoseMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        float angle = 36;
        float rotation = 225 + rotationAngle;

        poseStack.mulPose(new Quaternionf().rotationY(Mth.DEG_TO_RAD * -180));
        Quaternionf flip = new Quaternionf().rotationZ(Mth.DEG_TO_RAD * 180);
        flip.mul(new Quaternionf().rotationX(Mth.DEG_TO_RAD * angle));

        Quaternionf rotate = new Quaternionf().rotationY(Mth.DEG_TO_RAD * rotation);
        poseStack.mulPose(flip);
        poseStack.mulPose(rotate);
        poseStack.mulPose(new Quaternionf().rotationX(Mth.DEG_TO_RAD * -180));
    }

    private void renderBlocks(PoseStack poseStack, BufferSource bufferSource,
            BlockRenderDispatcher blockRenderer, BlueprintBlockInfo[] blocks, Vec3 center,
            float layerSpacing) {
        for (BlueprintBlockInfo blockInfo : blocks) {
            poseStack.pushPose();
            BlockPos pos = blockInfo.getRelativePosition();
            poseStack.translate(pos.getX() - center.x, pos.getY() * (layerSpacing + 1) - center.y,
                    pos.getZ() - center.z);
            blockRenderer.renderSingleBlock(blockInfo.getExpectedState(), poseStack, bufferSource,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    private void renderMaterials(GuiGraphics guiGraphics, MultiBlockBlueprint blueprint, int x,
            int y, int mouseX, int mouseY) {
        ArrayList<ItemStack> materials = new ArrayList<ItemStack>();
        if (blueprint != null) {
            BlueprintBlockInfo[] blocks = blueprint.getBlocks();
            materials = BlueprintBlockInfo.getItemStacks(blocks, false);
        }
        // Draw backgrounds as well as actual items
        ResourceLocation background = BlueprintList.BACKGROUND_SPRITES.get(true, false);
        int numRows = (int) Math.max(
                Math.ceil((float) materials.size() / BLUEPRINT_MATERIALS_ITEMS_PER_ROW),
                BLUEPRINT_MATERIALS_ITEM_ROWS);
        final int itemSlots = numRows * BLUEPRINT_MATERIALS_ITEMS_PER_ROW;
        guiGraphics.blitSprite(background, x, y, BLUEPRINT_MATERIALS_WIDTH,
                numRows * BLUEPRINT_MATERIALS_ITEM_HEIGHT
                        + (numRows - 1) * BLUEPRINT_MODEL_BUTTON_SPACING);
        for (int i = 0; i < itemSlots; i++) {
            int row = i / BLUEPRINT_MATERIALS_ITEMS_PER_ROW;
            int col = i % BLUEPRINT_MATERIALS_ITEMS_PER_ROW;
            int itemX = x + col * (BLUEPRINT_MATERIALS_ITEM_WIDTH + BLUEPRINT_MODEL_BUTTON_SPACING);
            int itemY =
                    y + row * (BLUEPRINT_MATERIALS_ITEM_HEIGHT + BLUEPRINT_MODEL_BUTTON_SPACING);
            // Background
            guiGraphics.blitSprite(EMPTY_BLOCK_SPRITE, itemX, itemY, BLUEPRINT_MATERIALS_ITEM_WIDTH,
                    BLUEPRINT_MATERIALS_ITEM_HEIGHT);
            // Items
            if (i < materials.size()) {
                itemX += (BLUEPRINT_MATERIALS_ITEM_WIDTH - ITEMSTACK_PIXEL_SIZE) / 2;
                itemY += (BLUEPRINT_MATERIALS_ITEM_HEIGHT - ITEMSTACK_PIXEL_SIZE) / 2;
                ItemStack itemStack = materials.get(i);
                guiGraphics.renderItem(itemStack, itemX, itemY);
                Minecraft minecraft = Minecraft.getInstance();
                guiGraphics.renderItemDecorations(minecraft.font, itemStack, itemX, itemY);
                // TODO: mouse over hitbox is wrong
                if (mouseX >= itemX && mouseX <= itemX + ITEMSTACK_PIXEL_SIZE && mouseY >= itemY
                        && mouseY <= itemY + ITEMSTACK_PIXEL_SIZE) {
                    guiGraphics.renderTooltip(minecraft.font, itemStack, mouseX, mouseY);
                }
            }
        }
    }
}

