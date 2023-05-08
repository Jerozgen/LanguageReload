package jerozgen.languagereload;

import jerozgen.languagereload.access.IAdvancementsScreen;
import jerozgen.languagereload.config.Config;
import jerozgen.languagereload.mixin.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;

@OnlyIn(Dist.CLIENT)
@Mod(LanguageReload.MOD_ID)
public class LanguageReload {
    public static final Logger LOGGER = LogManager.getLogger("Language Reload");
    public static final String MOD_ID = "languagereload";

    public static boolean shouldSetSystemLanguage = false;

    public LanguageReload() {
    }

    public static void reloadLanguages() {
        var client = Minecraft.getInstance();

        // Reload language and search managers
        client.getLanguageManager().onResourceManagerReload(client.getResourceManager());
        reloadSearch();

        // Update window title and chat
        client.updateTitle();
        client.gui.getChat().rescaleChat();

        // Update book and advancements screens
        if (client.screen instanceof BookViewScreen bookScreen) {
            ((BookScreenAccessor) bookScreen).languagereload_setCachedPageIndex(-1);
        } else if (client.screen instanceof AdvancementsScreen advancementsScreen) {
            ((IAdvancementsScreen) advancementsScreen).languagereload_recreateWidgets();
        }

        // Update signs
        if (client.level == null) return;
        var chunkManager = (ClientChunkManagerAccessor) client.level.getChunkSource();
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
        var client = Minecraft.getInstance();
        var searchManager = ((MinecraftClientAccessor) client).languagereload_getSearchManager();
        searchManager.onResourceManagerReload(client.getResourceManager());
    }

    @SuppressWarnings("ConstantValue")
    public static void setLanguage(String language, LinkedList<String> fallbacks) {
        var client = Minecraft.getInstance();
        var languageManager = client.getLanguageManager();
        var config = Config.getInstance();

        var languageIsSame = languageManager.getSelected().getCode().equals(language);
        var fallbacksAreSame = config.fallbacks.equals(fallbacks);
        if (languageIsSame && fallbacksAreSame) return;

        if (languageManager.getLanguage(language) == null)
            language = LanguageManager.DEFAULT_LANGUAGE_CODE;

        config.previousLanguage = languageManager.getSelected().getCode();
        config.previousFallbacks = config.fallbacks;
        config.language = language;
        config.fallbacks = fallbacks;
        Config.save();

        languageManager.setSelected(languageManager.getLanguage(language));
        client.options.languageCode = language;
        client.options.save();

        reloadLanguages();
    }
}