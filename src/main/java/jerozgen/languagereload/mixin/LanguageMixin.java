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
    @Unique private @Nullable ClientLanguage clientLanguage = null;
    @Unique private static @Nullable ClientLanguage clientLanguageOnSetInstance = null;


    @Inject(method = "inject", at = @At("HEAD"))
    private static void onInject(Language language, CallbackInfo ci) {
        if (language instanceof ClientLanguage clientLanguage) {
            clientLanguageOnSetInstance = clientLanguage;
        }
    }

    @Inject(method = "inject", at = @At("TAIL"))
    private static void afterInject(Language language, CallbackInfo ci) {
        ((ILanguage) language).languagereload_setClientLanguage(clientLanguageOnSetInstance);
        clientLanguageOnSetInstance = null;
    }

    @Override
    public void languagereload_setClientLanguage(ClientLanguage clientLanguage) {
        this.clientLanguage = clientLanguage;
    }

    @Override
    public ClientLanguage languagereload_getClientLanguage() {
        return clientLanguage;
    }
}
