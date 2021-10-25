package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IClientChunkMap;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(ClientChunkManager.ClientChunkMap.class)
public abstract class MixinClientChunkMap implements IClientChunkMap {
    @Shadow
    @Final
    AtomicReferenceArray<WorldChunk> chunks;

    @Override
    public AtomicReferenceArray<WorldChunk> getChunks() {
        return chunks;
    }
}