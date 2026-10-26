package org.hodytrapl.storyline.Client_side.chat.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.hodytrapl.storyline.Client_side.chat.model.ChatMessageData;
import org.hodytrapl.storyline.Client_side.chat.model.SenderType;
import org.jetbrains.annotations.NotNull;

public class ComposerScreen extends Screen {

    private EditBox messageInput;

    // Конструктор должен вызывать super() с заголовком экрана
    public ComposerScreen() {
        super(Component.literal("Storyline Input Chat"));
    }

    @Override
    protected void init() {
        super.init();

        final int boxWidth = 200;
        final int boxHeight = 20;

        // Центр по горизонтали
        int x = (this.width - boxWidth) / 2;
        int y = (int) (this.height * 0.85) - boxHeight / 2;
        this.messageInput = new EditBox(
                this.font,
                x, // x
                y, // y
                200,                  // width
                20,                   // height
                Component.literal("Введите сообщение...")// placeholder,
        );
        this.messageInput.setBordered(false);
        this.messageInput.setMaxLength(256);
        this.addRenderableWidget(this.messageInput);

        this.setFocused(this.messageInput);
        this.messageInput.setFocused(true);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        int x = this.messageInput.getX();
        int y = this.messageInput.getY();
        int w = this.messageInput.getWidth();
        int h = this.messageInput.getHeight();
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0x80000000); // Черный, 50% прозрачности
        super.render(graphics, mouseX, mouseY, partialTick);

        // Отрисовка элементов ПОСЛЕ виджетов (например, тултипы)
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter или Numpad Enter
            this.sendMessage();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void sendMessage() {
        String text = this.messageInput.getValue();

        if (text.trim().isEmpty()) {
            return;
        }

        // 1. Получаем имя игрока (клиентская сторона)
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        // === ВАЖНО: команды уходят через ванильный канал ===
        if (text.startsWith("/")) {
            // Vanilla ClientPacketListener.sendCommand(String command, @Nullable Component preview)
            player.connection.sendCommand(text.substring(1));
            this.messageInput.setValue("");
            return;
        }
        String username = player.getGameProfile().getName();

        // 2. Формируем модель
        ChatMessageData data = new ChatMessageData(
                username,
                "light_purple",              // цвет (можно вынести в конфиг/настройки)
                SenderType.PLAYER,
                text
        );

        // 3. Превращаем в NBT
        CompoundTag nbt = data.toNbt();

        this.messageInput.setValue(""); // Очищаем поле после отправки
        this.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;   // на всякий случай, для одиночной игры
    }
}