package jerozgen.languagereload.mixin;

import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LanguageManager.class)
public interface LanguageManagerAccessor {
    @Accessor("ENGLISH_US")
    static LanguageDefinition languagereload_getEnglishUs() {
        throw new AssertionError();
    }
}
