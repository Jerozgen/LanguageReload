package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.search.SearchManager;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends ReentrantThreadExecutor<Runnable> implements IMinecraftClient {
    @Shadow @Final private SearchManager searchManager;

    public MixinMinecraftClient(String string) {
        super(string);
    }

    @Override
    public SearchManager getSearchManager() {
        return searchManager;
    }
}
