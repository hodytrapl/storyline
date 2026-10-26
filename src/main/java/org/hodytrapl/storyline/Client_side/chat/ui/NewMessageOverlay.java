package org.hodytrapl.storyline.Client_side.chat.ui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.hodytrapl.storyline.Storyline;
import org.hodytrapl.storyline.Client_side.chat.model.ChatMessageData;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.util.List;

@EventBusSubscriber(modid = Storyline.MODID, value = Dist.CLIENT)
public class NewMessageOverlay implements LayeredDraw.Layer {

    public static final int DISPLAY_TICKS = 100;
    private static final int FADE_TICKS = 20;

    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(Storyline.MODID, "chat_message_overlay");

    private static ChatMessageData currentMessage;
    private static int ticksRemaining;

    public static void show(ChatMessageData message) {
        currentMessage = message;
        ticksRemaining = DISPLAY_TICKS;
    }

    public static void tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }
    }

    @SubscribeEvent
    public static void registerLayer(RegisterGuiLayersEvent event) {
        // Метод registerAboveAll принимает LayeredDraw.Layer
        event.registerAboveAll(LAYER_ID, new NewMessageOverlay());
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (currentMessage == null || ticksRemaining <= 0) return;

        // Пропускаем пустые сообщения (null, "", пробелы)
        String raw = currentMessage.message();
        if (raw == null || raw.isBlank()) return;

        var font = Minecraft.getInstance().font;
        var window = Minecraft.getInstance().getWindow();
        Component text = formatMessage(currentMessage);

        float alpha = Math.min(1f, ticksRemaining / (float) FADE_TICKS);
        int a = (int) (alpha * 255) << 24;

        int screenWidth  = window.getGuiScaledWidth();
        int screenHeight = window.getGuiScaledHeight();

        // 50% ширины экрана — максимум для строки
        int maxWidth = screenWidth / 2;
        List<FormattedCharSequence> lines = font.split(text, maxWidth);

        int lineHeight = font.lineHeight;
        int textHeight = lineHeight * lines.size();

        // Ширина блока — по самой широкой строке
        int textWidth = 0;
        for (FormattedCharSequence line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }

        int padX = Math.max(4, (int) (textWidth  * 0.10f));
        int padY = Math.max(3, (int) (textHeight * 0.10f));

        // Позиция блока (лево-верх)
        int blockX = (screenWidth - textWidth) / 2;
        int blockY = screenHeight - (int) (screenHeight * 0.15) - textHeight;

        // Фон
        int bgLeft   = blockX - padX;
        int bgTop    = blockY - padY;
        int bgRight  = blockX + textWidth  + padX;
        int bgBottom = blockY + textHeight + padY;

        int bgColor = ((int) (0x80 * alpha)) << 24;
        graphics.fill(bgLeft, bgTop, bgRight, bgBottom, bgColor);

        // Каждая строка — центрируется отдельно внутри блока
        for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence line = lines.get(i);
            int lineX = blockX + (textWidth - font.width(line)) / 2;
            graphics.drawString(font, line, lineX, blockY + i * lineHeight, 0xFFFFFF | a, true);
        }
    }

    private Component formatMessage(ChatMessageData msg) {
        int nameColor = parseColor(msg.color()) | 0xFF000000;
        return Component.literal("[" + msg.username() + "] ")
                .withStyle(style -> style.withColor(nameColor))
                .append(Component.literal(msg.message())
                        .withStyle(style -> style.withColor(0xFFFFFF)));
    }

    private int parseColor(String color) {
        if (color == null) return 0xFFFFFF;

        if (color.startsWith("#")) {
            try {
                String hex = color.substring(1);
                if (hex.length() == 6) {
                    return 0xFF000000 | Integer.parseInt(hex, 16);
                } else if (hex.length() == 8) {
                    return (int) Long.parseLong(hex, 16);
                }
            } catch (NumberFormatException ignored) {
            }
            return 0xFFFFFF;
        }

        ChatFormatting formatting = ChatFormatting.getByName(color.toLowerCase());
        return (formatting != null && formatting.getColor() != null)
                ? formatting.getColor()
                : 0xFFFFFF;
    }
}
