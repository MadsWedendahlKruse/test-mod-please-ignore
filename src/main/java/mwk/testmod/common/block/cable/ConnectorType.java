package mwk.testmod.common.block.cable;

import javax.annotation.Nonnull;
import net.minecraft.util.StringRepresentable;

public enum ConnectorType implements StringRepresentable {
    NONE, CABLE, BLOCK;

    public static final ConnectorType[] VALUES = values();

    @Override
    @Nonnull
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
