package mwk.testmod.common.network;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockUtils;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet for placing a blueprint controller. This allows the client to place the controller block
 * even if there's no surrounding blocks to place it on. This is useful when previewing the
 * blueprint using the hologram projector, since for most blueprints the controller is placed above
 * the ground.
 */
public record BuildMultiBlockPacket(ResourceKey<MultiBlockBlueprint> blueprintKey,
                                    BlockPos controllerPos, Direction controllerFacing)
        implements CustomPacketPayload {

    public static final Type<BuildMultiBlockPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TestMod.MODID, "blueprint_builder"));

    public static final StreamCodec<FriendlyByteBuf, BuildMultiBlockPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(TestModBlueprints.BLUEPRINT_REGISTRY_KEY),
            BuildMultiBlockPacket::blueprintKey,
            BlockPos.STREAM_CODEC,
            BuildMultiBlockPacket::controllerPos,
            Direction.STREAM_CODEC,
            BuildMultiBlockPacket::controllerFacing,
            BuildMultiBlockPacket::new);

    @Override
    public Type<BuildMultiBlockPacket> type() {
        return TYPE;
    }

    public static void handleServer(final BuildMultiBlockPacket packet,
            final IPayloadContext context) {
        Player player = context.player();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        context.enqueueWork(() -> {
            MultiBlockBlueprint blueprint = null;
            Level level = player.level();
            if (packet.blueprintKey() != null) {
                blueprint = level.registryAccess()
                        .registry(TestModBlueprints.BLUEPRINT_REGISTRY_KEY)
                        .flatMap(registry -> registry.getOptional(packet.blueprintKey()))
                        .orElse(null);
            }
            // TODO: Not sure if this even happens? I don't think we'd send the packet if the
            // blueprint is null
            if (blueprint == null) {
                TestMod.LOGGER.error(
                        "BuildMultiBlockPacket: Unable to build blueprint, received invalid blueprint key: {}",
                        packet.blueprintKey());
                return;
            }
            // Place the controller with the correct facing
            BlockPos controllerPos = packet.controllerPos();
            Direction controllerFacing = packet.controllerFacing();
            BlockState controllerState = blueprint.getController().defaultBlockState()
                    .setValue(MultiBlockControllerBlock.FACING, controllerFacing);
            MultiBlockUtils.attemptBuildMultiBlock(level, blueprint, controllerPos,
                    controllerFacing, player, null, true);
        });
    }

}
