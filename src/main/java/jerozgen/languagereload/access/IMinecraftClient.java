package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.search.SearchManager;

@Environment(EnvType.CLIENT)
public interface IMinecraftClient {
    SearchManager getSearchManager();
}
