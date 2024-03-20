package mwk.testmod.client.render.hologram.events;

import mwk.testmod.client.render.hologram.HologramRenderer;

public class ClearEvent implements HologramEvent {

    @Override
    public void apply(HologramRenderer renderer) {
        renderer.clearHologram();
    }
}
