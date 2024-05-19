package mwk.testmod.client.gui.widgets.panels.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

public class PanelManager {

    private Map<PanelSide, ArrayList<MachinePanel>> panels =
            new HashMap<PanelSide, ArrayList<MachinePanel>>();

    private int menuLeft;
    private int menuTop;
    private int menuWidth;
    private int menuHeight;
    private int leftOffset;
    private int topOffset;

    public PanelManager(int menuLeft, int menuTop, int menuWidth, int menuHeight, int leftOffset,
            int topOffset) {
        this.menuLeft = menuLeft;
        this.menuTop = menuTop;
        this.menuWidth = menuWidth;
        this.menuHeight = menuHeight;
        this.leftOffset = leftOffset;
        this.topOffset = topOffset;
        for (PanelSide side : PanelSide.values()) {
            panels.put(side, new ArrayList<MachinePanel>());
        }
    }

    public void addPanel(MachinePanel panel) {
        // TODO: Find a better way to decide which side to put the panel on
        if (panels.get(PanelSide.LEFT).size() <= panels.get(PanelSide.RIGHT).size()) {
            addPanel(panel, PanelSide.LEFT);
        } else {
            addPanel(panel, PanelSide.RIGHT);
        }
    }

    public void addPanel(MachinePanel panel, PanelSide side) {
        panels.get(side).add(panel);
        panel.setScreenPosition(menuLeft, menuTop);
    }

    public int getX(PanelSide side) {
        int x = menuLeft + leftOffset;
        if (side == PanelSide.RIGHT) {
            x += menuWidth;
        }
        return x;
    }

    public void renderPanelSide(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
            PanelSide side) {
        int y = menuTop + topOffset;
        for (MachinePanel panel : panels.get(side)) {
            panel.setPosition(getX(side), y, side);
            panel.render(guiGraphics, mouseX, mouseY, partialTick);
            y += panel.getHeight();
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (PanelSide side : PanelSide.values()) {
            renderPanelSide(guiGraphics, mouseX, mouseY, partialTick, side);
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        for (PanelSide side : PanelSide.values()) {
            for (MachinePanel panel : panels.get(side)) {
                if (panel.isMouseOver(mouseX, mouseY)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            // Only handle left clicks
            return;
        }
        for (PanelSide side : PanelSide.values()) {
            int clickedPanelIndex = -1;
            // Calculate the height of all the panels that are open
            int openPanelsHeight = 0;
            ArrayList<MachinePanel> panelsPanelSide = this.panels.get(side);
            for (int i = 0; i < panelsPanelSide.size(); i++) {
                MachinePanel panel = panelsPanelSide.get(i);
                boolean panelClicked = panel.mouseClicked(mouseX, mouseY, button);
                if (panel.isOpen()) {
                    openPanelsHeight += panel.getHeightOpen();
                    if (panelClicked) {
                        clickedPanelIndex = i;
                    }
                }
            }
            if (clickedPanelIndex == -1) {
                continue;
            }
            // Close the other panels until there is enough space for the clicked panel
            for (int i = 0; i < panelsPanelSide.size() && openPanelsHeight > menuHeight; i++) {
                MachinePanel panel = panelsPanelSide.get(i);
                if (i != clickedPanelIndex && panel.isOpen()) {
                    panel.close();
                    openPanelsHeight -= panel.getHeightOpen();
                }
            }
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (PanelSide side : PanelSide.values()) {
            for (MachinePanel panel : panels.get(side)) {
                if (panel.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX,
            double dragY) {
        for (PanelSide side : PanelSide.values()) {
            for (MachinePanel panel : panels.get(side)) {
                if (panel.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (PanelSide side : PanelSide.values()) {
            for (MachinePanel panel : panels.get(side)) {
                if (panel.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (PanelSide side : PanelSide.values()) {
            for (MachinePanel panel : panels.get(side)) {
                if (panel.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return A collection of areas that JEI should not render over.
     */
    public Collection<Rect2i> getGuiExtraAreas() {
        Collection<Rect2i> areas = new ArrayList<>();
        for (PanelSide side : PanelSide.values()) {
            for (MachinePanel panel : panels.get(side)) {
                areas.add(new Rect2i(panel.getX(), panel.getY(), panel.getWidth(),
                        panel.getHeight()));
            }
        }
        return areas;
    }
}
