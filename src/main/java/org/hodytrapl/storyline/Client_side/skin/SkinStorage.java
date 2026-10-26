package org.hodytrapl.storyline.Client_side.skin;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import org.hodytrapl.storyline.Utils.Config.ModPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import static org.hodytrapl.storyline.Storyline.MODID;
import static org.hodytrapl.storyline.Storyline.LOGGER;

public class SkinStorage {

    private static final String ASSET_PREFIX = "textures/skins/";

    private SkinStorage() {}

    public static Path skinPath(String name) {
        return ModPaths.SKINS.resolve(name + ".png");
    }

    //ищет все скины по пути и выгружает по одному
    public static void extractAllFromResources(){
        var rm = Minecraft.getInstance().getResourceManager();
        var found = rm.listResources(
                "textures/skins",
                loc -> loc.getPath().endsWith(".png")
        );

        if (found.isEmpty()) {
            LOGGER.info("No default skins found in mod resources");
            return;
        }

        int extracted = 0;//сколько получилось нам достать?
        for (var entry : found.entrySet()) {
            String path = entry.getKey().getPath();            // textures/skins/example.png
            String fileName = path.substring(ASSET_PREFIX.length()); // example.png
            String name = fileName.substring(0, fileName.length() - 4); // example
            if(extractOne(name, entry.getValue()))extracted++;
        }
        LOGGER.info("Extracted: [{}/{}]", extracted, ModPaths.SKINS);
    }

    // выгружаем один файл и место его нахождения
    private static boolean extractOne(String name, Resource res) {
        Path target = skinPath(name);
        if (Files.exists(target)) return true; //уже есть — пропускаем
        try (InputStream in = res.open()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Failed to extract skin {}", name, e);
            return false;
        }
        return true;
    }

//    если скин просят после включения мода
    public static boolean ensureFromResources(String name) {
        Path target = skinPath(name);
        if (Files.exists(target)) return true;

        var loc = ResourceLocation.fromNamespaceAndPath(
                MODID, ASSET_PREFIX + name + ".png");
        var opt = Minecraft.getInstance().getResourceManager().getResource(loc);
        if (opt.isEmpty()) return false;

        try (InputStream in = opt.get().open()) {
            Files.createDirectories(target.getParent());
            Files.copy(in, target);
            LOGGER.info("Extracted skin '{}' from resources", name);
            return true;
        } catch (IOException e) {
            LOGGER.error("Failed to extract skin {}", name, e);
            return false;
        }
    }

    //проверка имен скинов на старте
    public static void normalizeSkinFileNames(){
        try (var stream = Files.list(ModPaths.SKINS)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> {
                        String n = p.getFileName().toString();
                        return n.endsWith(".png") && !n.equals(n.toLowerCase(Locale.ROOT));
                    })
                    .forEach(p -> {
                        Path lower = p.resolveSibling(
                                p.getFileName().toString().toLowerCase(Locale.ROOT));
                        if (Files.exists(lower)) return;
                        try {
                            Files.move(p, lower);
                            LOGGER.info("Normalized skin file: {} -> {}", p.getFileName(), lower.getFileName());
                        } catch (IOException e) {
                            LOGGER.warn("Failed to rename {}", p, e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.error("Failed to scan skins folder", e);
        }
    }
}
