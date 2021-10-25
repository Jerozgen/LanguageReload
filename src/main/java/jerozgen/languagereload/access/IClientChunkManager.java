package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientChunkManager;

@Environment(EnvType.CLIENT)
public interface IClientChunkManager {
    ClientChunkManager.ClientChunkMap getChunks();
}
