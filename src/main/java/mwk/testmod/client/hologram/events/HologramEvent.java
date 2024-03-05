package mwk.testmod.client.hologram.events;

import mwk.testmod.client.hologram.HologramRenderer;

/**
 * Represents an event which causes a hologram to be rendered in a specific way.
 */
public interface HologramEvent {
    void apply(HologramRenderer renderer);
}
