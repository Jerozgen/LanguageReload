package jerozgen.languagereload;

import jerozgen.languagereload.access.*;
import jerozgen.languagereload.mixin.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;

@Environment(EnvType.CLIENT)
public class LanguageReload {
    public static void reloadLanguages(MinecraftClient client) {
        // Reload language and search managers
        var resourceManager = client.getResourceManager();
        client.getLanguageManager().reload(resourceManager);
        ((MinecraftClientAccessor) client).getSearchManager().reload(resourceManager);

        // Update window title and chat
        client.updateWindowTitle();
        client.inGameHud.getChatHud().reset();

        // Update book and advancements screens
        if (client.currentScreen instanceof BookScreen bookScreen) {
            ((BookScreenAccessor) bookScreen).setCachedPageIndex(-1);
        } else if (client.currentScreen instanceof AdvancementsScreen advancementsScreen) {
            ((IAdvancementsScreen) advancementsScreen).recreateWidgets();
        }

        // Update signs
        if (client.world == null) return;
        var chunkManager = (ClientChunkManagerAccessor) client.world.getChunkManager();
        var chunks = ((ClientChunkMapAccessor) chunkManager.getChunks()).getChunks();
        for (int i = 0; i < chunks.length(); i++) {
            var chunk = chunks.get(i);
            if (chunk == null) continue;
            for (var blockEntity : chunk.getBlockEntities().values()) {
                if (!(blockEntity instanceof SignBlockEntity sign)) continue;
                ((SignBlockEntityAccessor) sign).setTextsBeingEdited(null);
            }
        }
    }
}
