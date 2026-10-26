package org.hodytrapl.storyline.Client_side.chat.history;

import org.hodytrapl.storyline.Client_side.chat.model.ChatMessageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ChatHistory {
    private static final ChatHistory INSTANCE = new ChatHistory();
    private final List<ChatMessageData> messages = new ArrayList<>();
    private static final int MAX_MESSAGES = 100;

    private ChatHistory() {}

    public static ChatHistory get() { return INSTANCE; }

    public void add(ChatMessageData data) {
        messages.add(data);
        while (messages.size() > MAX_MESSAGES) {
            messages.removeFirst();
        }
    }

    public List<ChatMessageData> all() { return Collections.unmodifiableList(messages); }

    public void clear() { messages.clear(); }
}