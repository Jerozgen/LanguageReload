package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IBookScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BookScreen.class)
public abstract class MixinBookScreen extends Screen implements IBookScreen {
    @Shadow private int cachedPageIndex;

    protected MixinBookScreen(Text title) {
        super(title);
    }

    @Override
    public void clearCache() {
        cachedPageIndex = -1;
    }
}
