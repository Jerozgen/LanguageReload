package jerozgen.languagereload.mixin;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(ClientChunkCache.Storage.class)
public interface ClientChunkCacheStorageAccessor {
    @Accessor("chunks")
    AtomicReferenceArray<LevelChunk> languagereload_getChunks();
}
