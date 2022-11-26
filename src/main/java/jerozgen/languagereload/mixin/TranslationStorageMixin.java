package jerozgen.languagereload.mixin;

import com.google.common.collect.Maps;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(TranslationStorage.class)
abstract class TranslationStorageMixin extends Language implements ITranslationStorage {
    private @Nullable String targetLanguage;
    private static Map<String, Map<String, String>> separateTranslationsOnLoad;
    private Map<String, Map<String, String>> separateTranslations;

    @Inject(method = "<init>", at = @At("RETURN"))
    void onConstructed(Map<String, String> translations, boolean rightToLeft, CallbackInfo ci) {
        separateTranslations = separateTranslationsOnLoad;
        separateTranslationsOnLoad = null;
    }

    @Inject(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;)Lnet/minecraft/client/resource/language/TranslationStorage;",
            at = @At("HEAD"))
    private static void onLoad(ResourceManager resourceManager, List<LanguageDefinition> definitions, CallbackInfoReturnable<TranslationStorage> cir) {
        separateTranslationsOnLoad = Maps.newHashMap();
    }

    @Redirect(method = "load(Ljava/lang/String;Ljava/util/List;Ljava/util/Map;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/Language;load(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V"))
    private static void onInternalLoad$saveSeparately(InputStream inputStream, BiConsumer<String, String> entryConsumer, String langCode) {
        if (Config.getInstance().multilingualItemSearch) {
            Language.load(inputStream, entryConsumer.andThen((key, value) ->
                    separateTranslationsOnLoad.computeIfAbsent(langCode, k -> Maps.newHashMap()).put(key, value)));
        } else Language.load(inputStream, entryConsumer);
    }

    @Inject(method = "get", at = @At(value = "HEAD"), cancellable = true)
    void onGet(String key, CallbackInfoReturnable<String> cir) {
        if (!Config.getInstance().multilingualItemSearch) return;
        if (targetLanguage == null) return;

        var defaultTranslations = separateTranslations.get(Language.DEFAULT_LANGUAGE);
        if (defaultTranslations == null) return;

        var targetTranslations = separateTranslations.get(targetLanguage);
        if (targetTranslations == null) targetTranslations = defaultTranslations;

        cir.setReturnValue(targetTranslations.getOrDefault(key, defaultTranslations.getOrDefault(key, key)));
    }

    @Override
    public @Nullable String getTargetLanguage() {
        return targetLanguage;
    }

    @Override
    public void setTargetLanguage(@Nullable String value) {
        targetLanguage = value;
    }
}
