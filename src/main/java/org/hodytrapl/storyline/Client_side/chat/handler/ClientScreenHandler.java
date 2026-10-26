package org.hodytrapl.storyline.Client_side.chat.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.hodytrapl.storyline.Client_side.chat.ui.ComposerScreen;

import static org.hodytrapl.storyline.Storyline.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientScreenHandler {
    //открытие чата, заменяем на свой
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof ChatScreen) {
            event.setCanceled(true);

            Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(new ComposerScreen()));
        }
    }
}
