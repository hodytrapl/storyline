package org.hodytrapl.storyline.Client_side.chat.model;

import net.minecraft.nbt.CompoundTag;

public record ChatMessageData(String username,String color, SenderType senderType, String message) {
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("username", username);
        tag.putString("color", color);
        tag.putString("senderType", senderType.name());
        tag.putString("message", message);
        return tag;
    }

    public static ChatMessageData fromNbt(CompoundTag tag) {
        String username = tag.getString("username");
        String color = tag.getString("color");
        SenderType type = SenderType.valueOf(tag.getString("senderType"));
        String message = tag.getString("message");
        return new ChatMessageData(username,color, type, message);
    }
}
