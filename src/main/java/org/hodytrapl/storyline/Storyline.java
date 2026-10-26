package org.hodytrapl.storyline;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.hodytrapl.storyline.Client_side.skin.SkinStorage;
import org.hodytrapl.storyline.Utils.Config.Config;
import org.hodytrapl.storyline.Utils.Config.ModPaths;
import org.slf4j.Logger;

@Mod(Storyline.MODID)
public class Storyline {
    public static final String MODID = "storyline";
    public static final Logger LOGGER = LogUtils.getLogger();
    public Storyline(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!ModPaths.init()) {
            LOGGER.error("Skins will be unavailable: config folders not writable");
            return;
        }
        event.enqueueWork(() -> {
            SkinStorage.normalizeSkinFileNames();
            SkinStorage.extractAllFromResources();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ModPaths.init();
    }
}
