package org.hodytrapl.storyline.Client_side.chat.handler;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.hodytrapl.storyline.Storyline;
import org.hodytrapl.storyline.Client_side.chat.ui.NewMessageOverlay;

@EventBusSubscriber(modid = Storyline.MODID, value = Dist.CLIENT)
public class ClientTickHandler {

    //проверка каждый тик о появление сообщение в чате
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        NewMessageOverlay.tick();
    }
}