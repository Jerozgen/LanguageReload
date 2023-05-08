package jerozgen.languagereload.mixin;

import com.google.common.collect.Maps;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(ClientLanguage.class)
abstract class TranslationStorageMixin extends Language implements ITranslationStorage {
    @Unique private @Nullable String targetLanguage;
    private static String languageOnLoad;
    @Unique private static Map<String, Map<String, String>> separateTranslationsOnLoad;
    @Unique private Map<String, Map<String, String>> separateTranslations;

    @Inject(method = "<init>", at = @At("RETURN"))
    void onConstructed(Map<String, String> translations, boolean rightToLeft, CallbackInfo ci) {
        separateTranslations = separateTranslationsOnLoad;
        separateTranslationsOnLoad = null;
    }

    @Redirect(method = "loadFrom",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/language/LanguageInfo;getCode()Ljava/lang/String;"))
    private static String onLoad$language(LanguageInfo instance) {
        languageOnLoad = instance.getCode();
        return languageOnLoad;
    }

    @Inject(method = "loadFrom",
            at = @At("HEAD"))
    private static void onLoad(ResourceManager resourceManager, List<LanguageInfo> definitions, CallbackInfoReturnable<ClientLanguage> cir) {
        separateTranslationsOnLoad = Maps.newHashMap();
    }

    @Redirect(method = "appendFrom", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/locale/Language;loadFromJson(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V"))
    private static void onInternalLoad$saveSeparately(InputStream inputStream, BiConsumer<String, String> entryConsumer) {
        if (Config.getInstance().multilingualItemSearch) {
            Language.loadFromJson(inputStream, entryConsumer.andThen((key, value) ->
                    separateTranslationsOnLoad.computeIfAbsent(languageOnLoad, k -> Maps.newHashMap()).put(key, value)));
        } else Language.loadFromJson(inputStream, entryConsumer);
    }

    @Inject(method = "getOrDefault", at = @At(value = "HEAD"), cancellable = true)
    void onGet(String key, CallbackInfoReturnable<String> cir) {
        if (!Config.getInstance().multilingualItemSearch) return;
        if (targetLanguage == null) return;

        var defaultTranslations = separateTranslations.get(Language.DEFAULT);
        if (defaultTranslations == null) return;

        var targetTranslations = separateTranslations.get(targetLanguage);
        if (targetTranslations == null) targetTranslations = defaultTranslations;

        cir.setReturnValue(targetTranslations.getOrDefault(key, defaultTranslations.getOrDefault(key, key)));
    }

    @Override
    public @Nullable String languagereload_getTargetLanguage() {
        return targetLanguage;
    }

    @Override
    public void languagereload_setTargetLanguage(@Nullable String value) {
        targetLanguage = value;
    }
}