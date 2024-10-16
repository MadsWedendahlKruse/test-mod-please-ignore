package mwk.testmod.client.gui.widgets.buttons;

import java.util.ArrayList;
import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.datagen.TestModLanguageProvider;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BlueprintList extends AbstractScrollWidget {

    public static final WidgetSprites BACKGROUND_SPRITES =
            new WidgetSprites(new ResourceLocation(TestMod.MODID, "widget/button"),
                    new ResourceLocation(TestMod.MODID, "widget/button_highlighted"));
    // Same value as in AbstractScrollWidget, except this one is public
    public static final int SCROLL_BAR_WIDTH = 8;
    public static final int DEFAULT_PADDING = 4;
    public static final int DEFAULT_SPACING = 2;

    private final ArrayList<BlueprintButton> buttons;
    private String searchFilter;
    private ArrayList<BlueprintButton> filteredButtons;
    private ResourceKey<MultiBlockBlueprint> blueprintKey;
    private final int padding;
    private final int spacing;

    public BlueprintList(int x, int y, int width, int height, Component message) {
        this(x, y, width, height, DEFAULT_PADDING, DEFAULT_SPACING, message);
    }

    public BlueprintList(int x, int y, int width, int height, int padding, int spacing,
            Component message) {
        super(x, y, width - SCROLL_BAR_WIDTH, height, message);
        this.buttons = new ArrayList<BlueprintButton>();
        // Inital filter is empty
        filter("");
        this.padding = padding;
        this.spacing = spacing;
        addBlueprintButton(
                Component.translatable(TestModLanguageProvider.KEY_WIDGET_HOLOGRAM_PROJECTOR_NONE),
                null);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.level.registryAccess().lookup(TestModBlueprints.BLUEPRINT_REGISTRY_KEY)
                .ifPresent(blueprintRegistry -> {
                    blueprintRegistry.listElements().forEach(blueprint -> {
                        addBlueprintButton(Component.translatable(blueprint.value().getName()),
                                blueprint.key());
                    });
                });
    }

    private int getButtonY(int index) {
        return getY() + index * (Button.DEFAULT_HEIGHT + spacing) + spacing;
    }

    private void addBlueprintButton(Component buttonLabel,
            ResourceKey<MultiBlockBlueprint> blueprintKey) {
        buttons.add(new BlueprintButton(getX() + padding / 2, getButtonY(buttons.size()),
                width - padding, Button.DEFAULT_HEIGHT, buttonLabel, (pButton) -> {
            this.blueprintKey = blueprintKey;
        }));
    }

    public ResourceKey<MultiBlockBlueprint> getBlueprintKey() {
        return blueprintKey;
    }

    /**
     * Filters the buttons based on the given filter. The filter is case-insensitive and only
     * searches for the filter in the button's message.
     *
     * @param filter The filter to apply to the buttons
     */
    public void filter(String filter) {
        searchFilter = filter.toLowerCase();
        filteredButtons = new ArrayList<BlueprintButton>();
        int index = 0;
        for (BlueprintButton button : buttons) {
            if (searchFilter == null || searchFilter.isEmpty()
                    || button.getMessage().getString().toLowerCase().contains(searchFilter)) {
                button.setY(getButtonY(index));
                filteredButtons.add(button);
                index++;
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        // TODO Auto-generated method stub
    }

    @Override
    protected int getInnerHeight() {
        // TODO: Why does subtracting 3 * spacing work?
        return filteredButtons.size() * (Button.DEFAULT_HEIGHT + spacing) - 3 * spacing;
    }

    @Override
    protected double scrollRate() {
        return 1;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTick) {
        for (Button button : filteredButtons) {
            button.render(guiGraphics, mouseX, mouseY + (int) scrollAmount(), partialTick);
        }
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        boolean overScrollBar = scrollbarVisible()
                && pMouseX >= (double) (this.getX() + this.width)
                && pMouseX <= (double) (this.getX() + this.width + 8)
                && pMouseY >= (double) this.getY()
                && pMouseY < (double) (this.getY() + this.height);
        return super.isMouseOver(pMouseX, pMouseY) || overScrollBar;
    }

    public boolean buttonClicked(double pMouseX, double pMouseY, int pButton) {
        for (Button button : filteredButtons) {
            if (button.mouseClicked(pMouseX, pMouseY + scrollAmount(), pButton)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (Button button : filteredButtons) {
            if (button.mouseClicked(pMouseX, pMouseY + scrollAmount(), pButton)) {
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        for (Button button : filteredButtons) {
            if (button.isMouseOver(pMouseX, pMouseY + scrollAmount())) {
                button.onPress();
            }
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX,
            double pDragY) {
        setFocused(true);
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    protected void renderBorder(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight) {
        ResourceLocation resourceLocation = BACKGROUND_SPRITES.get(this.isActive(), false);
        pGuiGraphics.blitSprite(resourceLocation, pX, pY, pWidth, pHeight);
    }
}
