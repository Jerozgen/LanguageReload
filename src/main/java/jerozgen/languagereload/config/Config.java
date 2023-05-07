package jerozgen.languagereload.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;

@OnlyIn(Dist.CLIENT)
public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FMLPaths.CONFIGDIR.get().resolve(LanguageReload.MOD_ID + ".json");

    private static Config INSTANCE;

    public boolean multilingualItemSearch = true;
    public LinkedList<String> fallbacks = new LinkedList<>();
    public LinkedList<String> previousFallbacks = new LinkedList<>();
    public String language = "";
    public String previousLanguage = "";

    private Config() {}

    public static void load() {
        if (Files.notExists(PATH)) {
            INSTANCE = new Config();
            save();
        } else try {
            INSTANCE = GSON.fromJson(Files.readString(PATH), Config.class);
        } catch (Exception e) {
            INSTANCE = new Config();
            LanguageReload.LOGGER.error("Couldn't load config file: ", e);
        }

        var currentLang = Minecraft.getInstance().options.languageCode;
        if (!currentLang.equals(INSTANCE.language)) {
            INSTANCE.language = currentLang;
            INSTANCE.fallbacks.clear();
        }
    }

    public static void save() {
        if (INSTANCE == null) return;
        try {
            Files.write(PATH, Collections.singleton(GSON.toJson(INSTANCE)));
        } catch (Exception e) {
            LanguageReload.LOGGER.error("Couldn't save config file: ", e);
        }
    }

    public static Config getInstance() {
        return INSTANCE;
    }
}