package jerozgen.languagereload;

import jerozgen.languagereload.access.IAdvancementsScreen;
import jerozgen.languagereload.config.Config;
import jerozgen.languagereload.mixin.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

@Environment(EnvType.CLIENT)
public class LanguageReload {
    public static final Logger LOGGER = LogManager.getLogger("Language Reload");
    public static final String MOD_ID = "languagereload";

    public static final String NO_LANGUAGE_CODE = "*";
    public static final LanguageDefinition NO_LANGUAGE = new LanguageDefinition(NO_LANGUAGE_CODE, "*", "*", false) {
        @Override
        public String toString() {
            return "*";
        }
    };

    public static boolean shouldSetSystemLanguage = false;

    public static void reloadLanguages() {
        var client = MinecraftClient.getInstance();

        // Reload language and search managers
        client.getLanguageManager().reload(client.getResourceManager());
        reloadSearch();

        // Update window title and chat
        client.updateWindowTitle();
        client.inGameHud.getChatHud().reset();

        // Update book and advancements screens
        if (client.currentScreen instanceof BookScreen bookScreen) {
            ((BookScreenAccessor) bookScreen).languagereload_setCachedPageIndex(-1);
        } else if (client.currentScreen instanceof AdvancementsScreen advancementsScreen) {
            ((IAdvancementsScreen) advancementsScreen).languagereload_recreateWidgets();
        }

        // Update signs
        if (client.world == null) return;
        var chunkManager = (ClientChunkManagerAccessor) client.world.getChunkManager();
        var chunks = ((ClientChunkMapAccessor) chunkManager.languagereload_getChunks()).languagereload_getChunks();
        for (int i = 0; i < chunks.length(); i++) {
            var chunk = chunks.get(i);
            if (chunk == null) continue;
            for (var blockEntity : chunk.getBlockEntities().values()) {
                if (!(blockEntity instanceof SignBlockEntity sign)) continue;
                ((SignBlockEntityAccessor) sign).languagereload_setTextsBeingEdited(null);
            }
        }
    }

    public static void reloadSearch() {
        var client = MinecraftClient.getInstance();
        var searchManager = ((MinecraftClientAccessor) client).languagereload_getSearchManager();
        searchManager.reload(client.getResourceManager());
    }

    public static void setLanguage(String language, LinkedList<String> fallbacks) {
        var client = MinecraftClient.getInstance();
        var languageManager = client.getLanguageManager();
        var config = Config.getInstance();

        var languageIsSame = languageManager.getLanguage().getCode().equals(language);
        var fallbacksAreSame = config.fallbacks.equals(fallbacks);
        if (languageIsSame && fallbacksAreSame) return;

        var noLanguage = NO_LANGUAGE_CODE.equals(language);
        if (!noLanguage && languageManager.getLanguage(language) == null)
            language = LanguageManager.DEFAULT_LANGUAGE_CODE;

        config.previousLanguage = languageManager.getLanguage().getCode();
        config.previousFallbacks = config.fallbacks;
        config.language = language;
        config.fallbacks = fallbacks;
        Config.save();

        languageManager.setLanguage(noLanguage ? NO_LANGUAGE : languageManager.getLanguage(language));
        client.options.language = language;
        client.options.write();

        reloadLanguages();
    }
}