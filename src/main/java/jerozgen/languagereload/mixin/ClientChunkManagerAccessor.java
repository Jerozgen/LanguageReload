package jerozgen.languagereload.mixin;

import net.minecraft.client.multiplayer.ClientChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientChunkCache.class)
public interface ClientChunkManagerAccessor {
    @Accessor("storage")
    ClientChunkCache.Storage languagereload_getChunks();
}