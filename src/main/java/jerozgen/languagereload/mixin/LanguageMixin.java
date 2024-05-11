package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.ILanguage;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fixes Server Translation API incompatibility (#56)
@Mixin(value = Language.class, priority = 990)
public class LanguageMixin implements ILanguage {
    @Unique private @Nullable TranslationStorage translationStorage = null;
    @Unique private static @Nullable TranslationStorage translationStorageOnSetInstance = null;


    @Inject(method = "setInstance", at = @At("HEAD"))
    private static void onSetInstance(Language language, CallbackInfo ci) {
        if (language instanceof TranslationStorage translationStorage) {
            translationStorageOnSetInstance = translationStorage;
        }
    }

    @Inject(method = "setInstance", at = @At("TAIL"))
    private static void afterSetInstance(Language language, CallbackInfo ci) {
        ((ILanguage) language).languagereload_setTranslationStorage(translationStorageOnSetInstance);
        translationStorageOnSetInstance = null;
    }

    @Override
    public void languagereload_setTranslationStorage(TranslationStorage translationStorage) {
        this.translationStorage = translationStorage;
    }

    @Override
    public TranslationStorage languagereload_getTranslationStorage() {
        return translationStorage;
    }
}
