package jerozgen.languagereload.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.search.SearchManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor("searchManager")
    SearchManager languagereload_getSearchManager();
}
