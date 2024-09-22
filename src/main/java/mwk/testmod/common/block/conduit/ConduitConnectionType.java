package mwk.testmod.common.block.conduit;

import javax.annotation.Nonnull;
import net.minecraft.util.StringRepresentable;

public enum ConduitConnectionType implements StringRepresentable {
    NONE, CONDUIT, BIDIRECTIONAL, PULL, PUSH;

    public static final ConduitConnectionType[] VALUES = values();

    @Override
    @Nonnull
    public String getSerializedName() {
        return name().toLowerCase();
    }

    /**
     * @return {@code true} if this connection type can pull payloads, {@code false} otherwise
     */
    public boolean canPushPayload() {
        return this == BIDIRECTIONAL || this == PUSH;
    }

    /**
     * @return {@code true} if the model should render a connector for this connection type,
     * {@code false} otherwise
     */
    public boolean hasConnector() {
        return this == BIDIRECTIONAL || this == PUSH || this == PULL;
    }
}
