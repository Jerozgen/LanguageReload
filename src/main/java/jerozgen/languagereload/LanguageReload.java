package jerozgen.languagereload;

import jerozgen.languagereload.access.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.resource.ResourceManager;
import net.minecraft.world.chunk.WorldChunk;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Environment(EnvType.CLIENT)
public class LanguageReload {
    public static void reloadLanguages(MinecraftClient client) {
        // Reload language and search managers
        ResourceManager resourceManager = client.getResourceManager();
        client.getLanguageManager().reload(resourceManager);
        ((IMinecraftClient) client).getSearchManager().reload(resourceManager);

        // Update window title and chat
        client.updateWindowTitle();
        client.inGameHud.getChatHud().reset();

        // Update book and advancements screens
        if (client.currentScreen instanceof BookScreen bookScreen) {
            ((IBookScreen) bookScreen).clearCache();
        } else if (client.currentScreen instanceof AdvancementsScreen advancementsScreen) {
            ((IAdvancementsScreen) advancementsScreen).recreateWidgets();
        }

        // Update signs
        if (client.world == null) return;
        IClientChunkManager chunkManager = (IClientChunkManager) client.world.getChunkManager();
        AtomicReferenceArray<WorldChunk> chunks = ((IClientChunkMap) chunkManager.getChunks()).getChunks();
        for (int i = 0; i < chunks.length(); i++) {
            WorldChunk chunk = chunks.get(i);
            if (chunk == null) continue;
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (!(blockEntity instanceof SignBlockEntity sign)) continue;
                ((ISignBlockEntity) sign).clearCache();
            }
        }
    }
}
