package mwk.testmod.common.block.conduit;

import javax.annotation.Nonnull;
import net.minecraft.util.StringRepresentable;

public enum ConduitConnectionType implements StringRepresentable {
    NONE, CONDUIT, BIDIRECTIONAL, PULL, PUSH, DISABLED;

    public static final ConduitConnectionType[] VALUES = values();

    @Override
    @Nonnull
    public String getSerializedName() {
        return name().toLowerCase();
    }

    /**
     * @return {@code true} if this connection type can input payloads, {@code false} otherwise
     */
    public boolean canPushPayload() {
        return this == BIDIRECTIONAL || this == PUSH;
    }

    /**
     * @return {@code true} if the model should render a connector for this connection type,
     * {@code false} otherwise
     * <p>
     * TODO: Maybe the name of this method is a bit misleading, since DISABLED will also return true
     * even though it strictly speaking doesn't have a connector, although it does have a *potential*
     * connector, so we still want to render the hit-box to allow the player to change the connection
     * type.
     */
    public boolean hasConnector() {
        return this != NONE && this != CONDUIT;
    }
}
