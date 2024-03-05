package mwk.testmod.client.hologram.events;

import mwk.testmod.client.hologram.HologramRenderer;

public class ClearEvent implements HologramEvent {

    @Override
    public void apply(HologramRenderer renderer) {
        renderer.clearHologram();
    }
}
