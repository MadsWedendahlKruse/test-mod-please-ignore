package mwk.testmod.client.gui.widgets.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;

public class PanelManager {

    public enum Side {
        LEFT, RIGHT
    }

    private Map<Side, ArrayList<MachinePanel>> panels =
            new HashMap<Side, ArrayList<MachinePanel>>();

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
        for (Side side : Side.values()) {
            panels.put(side, new ArrayList<MachinePanel>());
        }
    }

    public void addPanel(MachinePanel panel) {
        // TODO: Find a better way to decide which side to put the panel on
        if (panels.get(Side.LEFT).size() <= panels.get(Side.RIGHT).size()) {
            addPanel(panel, Side.LEFT);
        } else {
            addPanel(panel, Side.RIGHT);
        }
    }

    public void addPanel(MachinePanel panel, Side side) {
        panels.get(side).add(panel);
        panel.setScreenPosition(menuLeft, menuTop);
    }

    public int getX(Side side) {
        int x = menuLeft + leftOffset;
        if (side == Side.RIGHT) {
            x += menuWidth;
        }
        return x;
    }

    public void renderSide(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
            Side side) {
        int y = menuTop + topOffset;
        for (MachinePanel panel : panels.get(side)) {
            panel.setPosition(getX(side), y, side == Side.LEFT);
            panel.render(guiGraphics, mouseX, mouseY, partialTick);
            y += panel.getHeight();
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Side side : Side.values()) {
            renderSide(guiGraphics, mouseX, mouseY, partialTick, side);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (Side side : Side.values()) {
            int clickedPanelIndex = -1;
            // Calculate the height of all the panels that are open
            int openPanelsHeight = 0;
            ArrayList<MachinePanel> panelsSide = this.panels.get(side);
            for (int i = 0; i < panelsSide.size(); i++) {
                MachinePanel panel = panelsSide.get(i);
                boolean panelClicked = panel.mouseClicked(mouseX, mouseY, button);
                if (panel.isOpen()) {
                    openPanelsHeight += panel.getHeight();
                    if (panelClicked) {
                        clickedPanelIndex = i;
                    }
                }
            }
            if (clickedPanelIndex == -1) {
                continue;
            }
            // Close the other panels until there is enough space for the clicked panel
            for (int i = 0; i < panelsSide.size() && openPanelsHeight > menuHeight; i++) {
                MachinePanel panel = panelsSide.get(i);
                if (i != clickedPanelIndex && panel.isOpen()) {
                    panel.close();
                    openPanelsHeight -= panel.getHeight();
                }
            }
        }
    }
}
