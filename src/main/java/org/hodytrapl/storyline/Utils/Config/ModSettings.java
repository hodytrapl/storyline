package org.hodytrapl.storyline.Utils.Config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.hodytrapl.storyline.Storyline;
import org.hodytrapl.storyline.Client_side.chat.ui.HistoryScreen;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Storyline.MODID, value = Dist.CLIENT)
public class ModSettings{
    public static final String KEY_CATEGORY = "key.categories.storyline";

    public static final KeyMapping OPEN_HISTORY = new KeyMapping(
            "key.storyline.open_history",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            KEY_CATEGORY
    );

    public static final KeyMapping ADVANCE_SCENE = new KeyMapping(
            "key.storyline.advance_scene",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            KEY_CATEGORY
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_HISTORY);
        event.register(ADVANCE_SCENE);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        // не открываем поверх других экранов / когда открыт чат
        while (OPEN_HISTORY.consumeClick()) {
            if (mc.screen == null) {
                mc.setScreen(new HistoryScreen());
            }
        }
    }
}
