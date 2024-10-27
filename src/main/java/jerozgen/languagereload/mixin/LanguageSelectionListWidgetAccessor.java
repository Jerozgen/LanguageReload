package jerozgen.languagereload.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.class)
public interface LanguageSelectionListWidgetAccessor {
    @Invoker("<init>")
    static LanguageOptionsScreen.LanguageSelectionListWidget languagereload_init(LanguageOptionsScreen screen, MinecraftClient client) {
        throw new AssertionError();
    }
}
