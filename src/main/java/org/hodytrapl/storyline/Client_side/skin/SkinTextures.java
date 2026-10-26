package org.hodytrapl.storyline.Client_side.skin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.hodytrapl.storyline.Utils.Config.ModPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.hodytrapl.storyline.Client_side.skin.SkinNames.isValidName;
import static org.hodytrapl.storyline.Client_side.skin.SkinStorage.ensureFromResources;
import static org.hodytrapl.storyline.Storyline.MODID;
import static org.hodytrapl.storyline.Storyline.LOGGER;

public class SkinTextures {

    private static ResourceLocation currentSkinTexture = null;

    private SkinTextures() {}

    public static ResourceLocation getCurrent() {
        return currentSkinTexture;
    }

    //сброс текстуры
    public static void reset() {
        if (currentSkinTexture == null) return;
        TextureManager tm = Minecraft.getInstance().getTextureManager();
        tm.getTexture(currentSkinTexture);
        tm.release(currentSkinTexture);
        currentSkinTexture = null;
    }

    public static ResourceLocation load(String rawName) {
        if (rawName == null) return null;

        String name = rawName.toLowerCase(Locale.ROOT);
        if (!isValidName(name)) {LOGGER.warn("Invalid skin name: '{}'", rawName);return null;}
        if (!SkinStorage.ensureFromResources(name)) {LOGGER.warn("Skin '{}' not found in config folder or mod resources", name);return null;}

        Path file = SkinStorage.skinPath(name);

        try (InputStream in = Files.newInputStream(file)) {
            NativeImage img = NativeImage.read(in);    // бросает IOException на битом PNG
            DynamicTexture tex = new DynamicTexture(img);

            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(
                    MODID, "skins/" + name);
            var tm = Minecraft.getInstance().getTextureManager();
            if (tm.getTexture(key) != null) tm.release(key); // освободить старую — иначе утечка
            tm.register(key, tex);
            return key;
        } catch (IOException e) {
            LOGGER.error("Failed to load skin {}", name, e);
            return null;
        }
    }
}
