package jerozgen.languagereload.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LanguageSelectScreen.LanguageSelectionList.class)
public interface LanguageSelectionListAccessor {
    @Invoker("<init>")
    static LanguageSelectScreen.LanguageSelectionList languagereload_init(LanguageSelectScreen screen, Minecraft minecraft) {
        throw new AssertionError();
    }
}
