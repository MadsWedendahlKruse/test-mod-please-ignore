package mwk.testmod.common.network;

import mwk.testmod.TestMod;
import mwk.testmod.common.block.multiblock.MultiBlockControllerBlock;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockBlueprint;
import mwk.testmod.common.block.multiblock.blueprint.MultiBlockUtils;
import mwk.testmod.init.registries.TestModBlueprints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Packet for placing a blueprint controller. This allows the client to place the controller block
 * even if there's no surrounding blocks to place it on. This is useful when previewing the
 * blueprint using the hologram projector, since for most blueprints the controller is placed above
 * the ground.
 */
public record BuildMultiBlockPacket(ResourceKey<MultiBlockBlueprint> blueprintKey,
                                    BlockPos controllerPos, Direction controllerFacing)
        implements CustomPacketPayload {

    public static final ResourceLocation ID =
            new ResourceLocation(TestMod.MODID, "blueprint_builder");

    public BuildMultiBlockPacket(FriendlyByteBuf buffer) {
        this(buffer.readResourceKey(TestModBlueprints.BLUEPRINT_REGISTRY_KEY),
                buffer.readBlockPos(), buffer.readEnum(Direction.class));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceKey(blueprintKey());
        buffer.writeBlockPos(controllerPos());
        buffer.writeEnum(controllerFacing());
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void handleServer(final BuildMultiBlockPacket packet,
            PlayPayloadContext context) {
        TestMod.LOGGER.debug("Received blueprint builder packet: {}, side: {}", packet,
                context.level().get().isClientSide());
        Player player = context.player().orElse(null);
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        context.workHandler().execute(() -> {
            MultiBlockBlueprint blueprint = null;
            Level level = player.level();
            if (packet.blueprintKey() != null) {
                blueprint =
                        level.registryAccess().registry(TestModBlueprints.BLUEPRINT_REGISTRY_KEY)
                                .flatMap(registry -> registry.getOptional(packet.blueprintKey()))
                                .orElse(null);
            }
            // TODO: Not sure if this even happens? I don't think we'd send the packet if the
            // blueprint is null
            if (blueprint == null) {
                TestMod.LOGGER.error("BlueprintBuilderPacket: Received invalid blueprint key: {}",
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
