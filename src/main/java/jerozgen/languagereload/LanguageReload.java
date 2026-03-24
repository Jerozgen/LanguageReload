package jerozgen.languagereload;

import com.mojang.blaze3d.platform.InputConstants;
import jerozgen.languagereload.access.IAdvancementsScreen;
import jerozgen.languagereload.config.Config;
import jerozgen.languagereload.mixin.BookViewScreenAccessor;
import jerozgen.languagereload.mixin.ClientChunkCacheAccessor;
import jerozgen.languagereload.mixin.ClientChunkCacheStorageAccessor;
import jerozgen.languagereload.mixin.SignTextAccessor;
import jerozgen.languagereload.mixin.TextDisplayAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.locale.Language;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;

@Environment(EnvType.CLIENT)
public class LanguageReload implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Language Reload");
    public static final String MOD_ID = "languagereload";

    public static KeyMapping reloadLanguagesKey;

    public static boolean shouldSetSystemLanguage = false;

    @Override
    public void onInitializeClient() {
        reloadLanguagesKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.debug.reloadLanguages",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                KeyMapping.Category.DEBUG
        ));
    }

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
            var chunks = ((ClientChunkCacheStorageAccessor) (Object) chunkManager.languagereload_getStorage()).languagereload_getChunks();
            for (int i = 0; i < chunks.length(); i++) {
                var chunk = chunks.get(i);
                if (chunk == null) continue;
                for (var blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof SignBlockEntity sign)) continue;
                    ((SignTextAccessor) sign.getFrontText()).languagereload_setRenderMessages(null);
                    ((SignTextAccessor) sign.getBackText()).languagereload_setRenderMessages(null);
                }
            }

            // Update text displays
            for (var entity : client.level.entitiesForRendering()) {
                if (entity instanceof Display.TextDisplay textDisplay) {
                    ((TextDisplayAccessor) textDisplay).languagereload_setClientDisplayCache(null);
                }
            }
        }
    }

    public static void setLanguage(@Nullable String language) {
        if (language == null || language.equals(Config.NO_LANGUAGE)) {
            setLanguage(Config.NO_LANGUAGE, null);
        } else if (language.equals(Language.DEFAULT)) {
            setLanguage(Language.DEFAULT, null);
        } else {
            setLanguage(language, new LinkedList<>() {{ add(Language.DEFAULT); }});
        }
    }

    public static void setLanguage(
            @Nullable String language,
            @Nullable LinkedList<@NotNull String> fallbacks
    ) {
        var newLanguage = language == null ? Config.NO_LANGUAGE : language;
        var newFallbacks = fallbacks == null ? new LinkedList<String>() : fallbacks;

        var client = Minecraft.getInstance();
        var languageManager = client.getLanguageManager();
        var config = Config.getInstance();

        var languageIsSame = languageManager.getSelected().equals(newLanguage);
        var fallbacksAreSame = config.fallbacks.equals(newFallbacks);
        if (languageIsSame && fallbacksAreSame) return;

        config.previousLanguage = languageManager.getSelected();
        config.previousFallbacks = config.fallbacks;
        config.language = newLanguage;
        config.fallbacks = newFallbacks;
        Config.save();

        languageManager.setSelected(newLanguage);
        client.options.languageCode = newLanguage;
        client.options.save();

        reloadLanguages();
    }

    public static @NotNull LinkedList<@NotNull String> getLanguages() {
        var list = new LinkedList<String>();
        var language = Minecraft.getInstance().getLanguageManager().getSelected();
        if (!language.equals(Config.NO_LANGUAGE)) {
            list.add(language);
        }
        list.addAll(Config.getInstance().fallbacks);
        return list;
    }
}