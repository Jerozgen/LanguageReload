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
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

@Environment(EnvType.CLIENT)
public class LanguageReload {
    public static final Logger LOGGER = LogManager.getLogger("Language Reload");
    public static final String MOD_ID = "languagereload";

    public static final String NO_LANGUAGE = "*";

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

        if (client.world != null) {
            // Update signs
            var chunkManager = (ClientChunkManagerAccessor) client.world.getChunkManager();
            var chunks = ((ClientChunkMapAccessor) chunkManager.languagereload_getChunks()).languagereload_getChunks();
            for (int i = 0; i < chunks.length(); i++) {
                var chunk = chunks.get(i);
                if (chunk == null) continue;
                for (var blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof SignBlockEntity sign)) continue;
                    ((SignTextAccessor) sign.getFrontText()).languagereload_setOrderedMessages(null);
                    ((SignTextAccessor) sign.getBackText()).languagereload_setOrderedMessages(null);
                }
            }

            // Update text displays
            for (var entity : client.world.getEntities()) {
                if (entity instanceof DisplayEntity.TextDisplayEntity textDisplay) {
                    ((TextDisplayEntityAccessor) textDisplay).languagereload_setTextLines(null);
                }
            }
        }
    }

    public static void reloadSearch() {
        var client = MinecraftClient.getInstance();
        var searchManager = ((MinecraftClientAccessor) client).languagereload_getSearchManager();
        searchManager.reload(client.getResourceManager());
    }

    public static void setLanguage(@Nullable String language) {
        if (language == null || language.equals(NO_LANGUAGE)) {
            setLanguage(NO_LANGUAGE, null);
        } else if (language.equals(Language.DEFAULT_LANGUAGE)) {
            setLanguage(Language.DEFAULT_LANGUAGE, null);
        } else {
            setLanguage(language, new LinkedList<>() {{ add(Language.DEFAULT_LANGUAGE); }});
        }
    }

    public static void setLanguage(
            @Nullable String language,
            @Nullable LinkedList<@NotNull String> fallbacks
    ) {
        var newLanguage = language == null ? NO_LANGUAGE : language;
        var newFallbacks = fallbacks == null ? new LinkedList<String>() : fallbacks;
        var client = MinecraftClient.getInstance();
        var languageManager = client.getLanguageManager();
        var config = Config.getInstance();

        var languageIsSame = languageManager.getLanguage().equals(newLanguage);
        var fallbacksAreSame = config.fallbacks.equals(newFallbacks);
        if (languageIsSame && fallbacksAreSame) return;

        config.previousLanguage = languageManager.getLanguage();
        config.previousFallbacks = config.fallbacks;
        config.language = newLanguage;
        config.fallbacks = newFallbacks;
        Config.save();

        languageManager.setLanguage(newLanguage);
        client.options.language = newLanguage;
        client.options.write();

        reloadLanguages();
    }

    public static @NotNull LinkedList<@NotNull String> getLanguages() {
        var list = new LinkedList<String>();
        var language = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        if (!language.equals(LanguageReload.NO_LANGUAGE)) {
            list.add(language);
        }
        list.addAll(Config.getInstance().fallbacks);
        return list;
    }
}