package jerozgen.languagereload.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.SearchRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {
    @Accessor("searchRegistry")
    SearchRegistry languagereload_getSearchManager();
}