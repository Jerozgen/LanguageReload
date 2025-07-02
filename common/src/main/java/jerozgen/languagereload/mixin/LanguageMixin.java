package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.ILanguage;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fixes Server Translation API incompatibility (#56)
@Mixin(value = Language.class, priority = 990)
public class LanguageMixin implements ILanguage {
    @Unique private static @Nullable ClientLanguage languagereload_clientLanguageOnSetInstance = null;
    @Unique private @Nullable ClientLanguage languagereload_clientLanguage = null;

    @Inject(method = "inject", at = @At("HEAD"))
    private static void onSetInstance(Language language, CallbackInfo ci) {
        if (language instanceof ClientLanguage clientLanguage) {
            languagereload_clientLanguageOnSetInstance = clientLanguage;
        }
    }

    @Inject(method = "inject", at = @At("TAIL"))
    private static void afterSetInstance(Language language, CallbackInfo ci) {
        ((ILanguage) language).languagereload_setClientLanguage(languagereload_clientLanguageOnSetInstance);
        languagereload_clientLanguageOnSetInstance = null;
    }

    @Override
    public void languagereload_setClientLanguage(ClientLanguage translationStorage) {
        this.languagereload_clientLanguage = translationStorage;
    }

    @Override
    public ClientLanguage languagereload_getClientLanguage() {
        return languagereload_clientLanguage;
    }
}
