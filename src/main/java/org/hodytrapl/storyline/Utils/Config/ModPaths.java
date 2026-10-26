package org.hodytrapl.storyline.Utils.Config;

import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hodytrapl.storyline.Storyline.MODID;

public final class ModPaths {

    private static final Logger LOGGER = LoggerFactory.getLogger(MODID+"/Paths");

    public static final Path ROOT = FMLPaths.CONFIGDIR.get().resolve(MODID);

    public static final Path CONFIGS = ROOT.resolve("configs");

    public static final Path SKINS = ROOT.resolve("skins");

    public static final Path CACHE = ROOT.resolve("cache");

    private ModPaths() {}

    public static boolean init() {
        boolean ok = createDir(ROOT)
                & createDir(CONFIGS)
                & createDir(SKINS)
                & createDir(CACHE);
        if (ok) {
            LOGGER.info("ModPaths initialized at: {}", ROOT.toAbsolutePath());
        } else {
            LOGGER.error("ModPaths initialization incomplete at: {}", ROOT.toAbsolutePath());
        }
        return ok;
    }

    private static boolean createDir(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                LOGGER.info("Created directory: {}", path);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create directory: {}", path, e);
            return false;
        }
        return true;
    }

    public static Path config(String fileName) {
        return CONFIGS.resolve(fileName);
    }
}
