package jerozgen.languagereload.mixin;

import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LanguageManager.class)
public interface LanguageManagerAccessor {
    @Accessor("DEFAULT_LANGUAGE")
    static LanguageInfo languagereload_getDefaultLanguage() {
        throw new AssertionError();
    }
}
