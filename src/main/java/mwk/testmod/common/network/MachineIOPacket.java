package mwk.testmod.common.network;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.entity.base.MachineBlockEntity;
import mwk.testmod.common.block.inventory.base.MachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet for setting machine IO settings.
 */
public record MachineIOPacket(boolean input, boolean value, BlockPos blockPos) implements
        CustomPacketPayload {

    public static final Type<MachineIOPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "machine_io"));

    public static final StreamCodec<FriendlyByteBuf, MachineIOPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            MachineIOPacket::input,
            ByteBufCodecs.BOOL,
            MachineIOPacket::value,
            BlockPos.STREAM_CODEC,
            MachineIOPacket::blockPos,
            MachineIOPacket::new);

    @Override
    public Type<MachineIOPacket> type() {
        return TYPE;
    }

    public static void handleServer(final MachineIOPacket packet, final IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer) || !(player.containerMenu instanceof MachineMenu)) {
            return;
        }
        context.enqueueWork(() -> {
            BlockEntity blockEntity = player.level().getBlockEntity(packet.blockPos());
            if (blockEntity instanceof MachineBlockEntity machineBlockEntity) {
                if (packet.input()) {
                    machineBlockEntity.setAutoPull(packet.value());
                } else {
                    machineBlockEntity.setAutoPush(packet.value());
                }
            }
        });
    }
}
