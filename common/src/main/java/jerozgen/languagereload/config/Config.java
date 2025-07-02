package jerozgen.languagereload.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.platform.Services;
import net.minecraft.locale.Language;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = Services.PLATFORM.getConfigDir().resolve(LanguageReload.MOD_ID + ".json");

    private static Config INSTANCE;

    public int version = 0;

    public boolean multilingualItemSearch = true;
    public boolean removableDefaultLanguage = false;
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

        migrateToVersion1();

        if (INSTANCE.language.equals(LanguageReload.NO_LANGUAGE) && !INSTANCE.fallbacks.isEmpty()) {
            INSTANCE.language = INSTANCE.fallbacks.pollFirst();
        }
        if (INSTANCE.previousLanguage.equals(LanguageReload.NO_LANGUAGE) && !INSTANCE.previousFallbacks.isEmpty()) {
            INSTANCE.previousLanguage = INSTANCE.previousFallbacks.pollFirst();
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
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    private static void migrateToVersion1() {
        if (INSTANCE.version >= 1) return;
        INSTANCE.version = 1;

        if (!INSTANCE.language.isEmpty()
                && !INSTANCE.language.equals(Language.DEFAULT)
                && !INSTANCE.fallbacks.contains(Language.DEFAULT))
            INSTANCE.fallbacks.add(Language.DEFAULT);

        if (!INSTANCE.previousLanguage.isEmpty()
                && !INSTANCE.previousLanguage.equals(Language.DEFAULT)
                && !INSTANCE.previousFallbacks.contains(Language.DEFAULT))
            INSTANCE.previousFallbacks.add(Language.DEFAULT);

        save();
    }
}
