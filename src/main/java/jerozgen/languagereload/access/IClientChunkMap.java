package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.chunk.WorldChunk;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Environment(EnvType.CLIENT)
public interface IClientChunkMap {
    AtomicReferenceArray<WorldChunk> getChunks();
}
