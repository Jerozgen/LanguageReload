package jerozgen.languagereload;

import jerozgen.languagereload.access.IAdvancementsScreen;
import jerozgen.languagereload.config.Config;
import jerozgen.languagereload.mixin.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

public class LanguageReload {
    public static final String MOD_NAME = "Language Reload";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final String MOD_ID = "languagereload";

    public static final String NO_LANGUAGE = "*";

    public static boolean shouldSetSystemLanguage = false;

    public static void reloadLanguages() {
        var client = Minecraft.getInstance();

        // Reload language manager
        client.getLanguageManager().onResourceManagerReload(client.getResourceManager());

        // Update window title and chat
        client.updateTitle();
        client.gui.getChat().rescaleChat();

        // Update book and advancements screens
        if (client.screen instanceof BookViewScreen bookScreen) {
            ((BookViewScreenAccessor) bookScreen).languagereload_setCachedPage(-1);
        } else if (client.screen instanceof AdvancementsScreen advancementsScreen) {
            ((IAdvancementsScreen) advancementsScreen).languagereload_recreateWidgets();
        }

        if (client.level != null) {
            // Update signs
            var chunkManager = (ClientChunkCacheAccessor) client.level.getChunkSource();
            var chunks = ((ClientChunkCacheStorageAccessor)(Object) chunkManager.languagereload_getChunks()).languagereload_getChunks();
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
            for (var entity : client.level.entitiesForRendering()) {
                if (entity instanceof Display.TextDisplay textDisplay) {
                    ((TextDisplayAccessor) textDisplay).languagereload_setTextLines(null);
                }
            }
        }
    }

    public static void setLanguage(String language, LinkedList<String> fallbacks) {
        var client = Minecraft.getInstance();
        var languageManager = client.getLanguageManager();
        var config = Config.getInstance();

        var languageIsSame = languageManager.getSelected().equals(language);
        var fallbacksAreSame = config.fallbacks.equals(fallbacks);
        if (languageIsSame && fallbacksAreSame) return;

        config.previousLanguage = languageManager.getSelected();
        config.previousFallbacks = config.fallbacks;
        config.language = language;
        config.fallbacks = fallbacks;
        Config.save();

        languageManager.setSelected(language);
        client.options.languageCode = language;
        client.options.save();

        reloadLanguages();
    }
}