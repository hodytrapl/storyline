package org.hodytrapl.storyline.Client_side.chat.ui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.hodytrapl.storyline.Client_side.chat.history.ChatHistory;
import org.hodytrapl.storyline.Client_side.chat.model.ChatMessageData;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class HistoryScreen extends Screen {

    private static final int PADDING = 10;
    private static final int LINE_HEIGHT = 10;
    private static final int ENTRY_SPACING = 6;
    private static final int SCROLL_SPEED = 24;

    private double scrollAmount = 0;
    private int maxScroll = 0;

    // Конструктор должен вызывать super() с заголовком экрана
    public HistoryScreen() {
        super(Component.literal("Storyline History Chat"));
    }

    @Override
    protected void init() {
        super.init();
        this.scrollAmount = 0;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xC0000000);

        super.render(graphics, mouseX, mouseY, partialTick);

        int boxWidth  = Math.min(420, this.width  - 40);
        int boxHeight = Math.min(360, this.height - 60);
        int x0 = (this.width  - boxWidth)  / 2;
        int y0 = (this.height - boxHeight) / 2;
        int x1 = x0 + boxWidth;
        int y1 = y0 + boxHeight;

        // рамка + фон панели
        graphics.fill(x0 - 1, y0 - 1, x1 + 1, y1 + 1, 0xFF000000);
        graphics.fill(x0, y0, x1, y1, 0xE0202020);

        // заголовок
//        graphics.drawString(this.font, this.title, x0 + PADDING, y0 + PADDING, 0xFFFFFFFF);

        //int contentTop    = y0 + PADDING + LINE_HEIGHT + 4;
        int contentTop = y0+PADDING;
        int contentBottom = y1 - PADDING;
        int contentHeight = contentBottom - contentTop;
        int textWidth     = boxWidth - PADDING * 2 - 6; // минус место под скроллбар

        List<ChatMessageData> messages = ChatHistory.get().all();

        // 1) считаем общую высоту с переносом строк
        int totalHeight = 0;
        for (ChatMessageData data : messages) {
            totalHeight += wrapHeight(data, textWidth) + ENTRY_SPACING;
        }
        this.maxScroll = Math.max(0, totalHeight - contentHeight);
        this.scrollAmount = Mth.clamp(this.scrollAmount, 0, this.maxScroll);

        // 2) рисуем под scissor
        graphics.enableScissor(x0 + 1, contentTop, x1 - 1, contentBottom);

        int y = contentTop - (int) this.scrollAmount;
        for (ChatMessageData data : messages) {
            y = renderEntry(graphics, data, x0 + PADDING, y, textWidth);
            y += ENTRY_SPACING;
        }

        graphics.disableScissor();

        if (this.scrollAmount > 0) {
            int fadeHeight = contentHeight * 15 / 100;      // 15% от высоты контента
            int fadeEnd    = contentTop + fadeHeight;

            graphics.fillGradient(
                    x0 + 1, contentTop,     // левый-верх
                    x1 - 1, fadeEnd,        // правый-низ
                    0xE0202020,             // вверху: как фон панели
                    0x00202020              // внизу: полностью прозрачно
            );
        }

        // 3) скроллбар
        if (this.maxScroll > 0) {
            int barX = x1 - PADDING + 2;
            int barTop = contentTop;
            int barBottom = contentBottom;
            int barHeight = barBottom - barTop;

            graphics.fill(barX, barTop, barX + 4, barBottom, 0xFF000000);

            float ratio = (float) contentHeight / totalHeight;
            int thumbHeight = Math.max(16, (int) (barHeight * ratio));
            float scrollRatio = (float) this.scrollAmount / this.maxScroll;
            int thumbY = barTop + (int) ((barHeight - thumbHeight) * scrollRatio);

            graphics.fill(barX, thumbY, barX + 4, thumbY + thumbHeight, 0xFFAAAAAA);
        }
    }

    private int wrapHeight(ChatMessageData data, int width) {
        Component name = Component.literal("[" + data.username() + "]");
        Component text = Component.literal(data.message());
        return (this.font.split(name, width).size()
                + this.font.split(text, width).size()) * LINE_HEIGHT;
    }

    private int renderEntry(GuiGraphics graphics, ChatMessageData data, int x, int y, int width) {
        // ЗДЕСЬ подставь реальные поля/методы ChatMessageData
        Component name = Component.literal("[" + data.username() + "]")
                .withStyle(style -> style.withColor(parseColor(data.color()) | 0xFF000000));

        for (var seq : this.font.split(name, width)) {
            int cx = x + (width - this.font.width(seq)) / 2;
            graphics.drawString(this.font, seq, cx, y, 0xFFFFFF, false);
            y += LINE_HEIGHT;
        }

        // сообщение
        Component text = Component.literal(data.message())
                .withStyle(style -> style.withColor(0xFFFFFF));

        for (var seq : this.font.split(text, width)) {
            int cx = x + (width - this.font.width(seq)) / 2;
            graphics.drawString(this.font, seq, cx, y, 0xFFFFFF, false);
            y += LINE_HEIGHT;
        }

        return y;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.maxScroll > 0) {
            this.scrollAmount = Mth.clamp(
                    this.scrollAmount - scrollY * SCROLL_SPEED, 0, this.maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_H || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }


    @Override
    public boolean isPauseScreen() {
        return false;   // на всякий случай, для одиночной игры
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