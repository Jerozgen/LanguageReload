package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IClientChunkManager;
import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientChunkManager.class)
public abstract class MixinClientChunkManager implements IClientChunkManager {
    @Shadow volatile ClientChunkManager.ClientChunkMap chunks;

    @Override
    public ClientChunkManager.ClientChunkMap getChunks() {
        return chunks;
    }
}
