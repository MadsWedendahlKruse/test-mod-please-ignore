package mwk.testmod.common.network;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Packet for setting machine IO settings.
 */
public record MachineIOPacket(Type type, boolean value, BlockPos blockPos)
        implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(TestMod.MODID, "machine_io");

    public enum Type {
        AUTO_INSERT, AUTO_EJECT
    }

    public MachineIOPacket(FriendlyByteBuf buffer) {
        this(buffer.readEnum(Type.class), buffer.readBoolean(), buffer.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(type());
        buffer.writeBoolean(value());
        buffer.writeBlockPos(blockPos());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void handleServer(final MachineIOPacket packet, PlayPayloadContext context) {
        TestMod.LOGGER.debug("Received machine io packet: {}, side: {}", packet,
                context.level().get().isClientSide());
        Player player = context.player().orElse(null);
        if (!(player instanceof ServerPlayer) || !(player.containerMenu instanceof MachineMenu)) {
            return;
        }
        context.workHandler().execute(() -> {
            BlockEntity blockEntity = player.level().getBlockEntity(packet.blockPos());
            if (blockEntity instanceof MachineBlockEntity machineBlockEntity) {
                switch (packet.type()) {
                    case AUTO_INSERT -> machineBlockEntity.setAutoPull(packet.value());
                    case AUTO_EJECT -> machineBlockEntity.setAutoPush(packet.value());
                }
            }
        });
    }
}
