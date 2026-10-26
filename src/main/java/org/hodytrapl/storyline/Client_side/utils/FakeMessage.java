package org.hodytrapl.storyline.Client_side.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.hodytrapl.storyline.Client_side.chat.model.ChatMessageData;
import org.hodytrapl.storyline.Client_side.chat.model.SenderType;
import org.hodytrapl.storyline.server_side.ChatModule.Payload.ChatMessagePacket;

public final class FakeMessage {

    private FakeMessage() {}

    public static void sayAsPlayer(ServerPlayer player, String chatColor, String message) {
        if (player == null) return;
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel)) return;

        ChatMessageData data = new ChatMessageData(
                player.getName().getString(),
                chatColor,
                SenderType.PLAYER,
                message
        );
        CompoundTag nbt = data.toNbt();
        PacketDistributor.sendToPlayer(player, new ChatMessagePacket(nbt));
    }

    public static void broadcastAsPlayer(ServerPlayer player, String chatColor, String message) {
        if (player == null) return;

        ChatMessageData data = new ChatMessageData(
                player.getName().getString(),
                chatColor,
                SenderType.PLAYER,
                message
        );
        CompoundTag nbt = data.toNbt();
        PacketDistributor.sendToAllPlayers(new ChatMessagePacket(nbt));
    }
}
