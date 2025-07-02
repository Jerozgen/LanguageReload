package jerozgen.languagereload.mixin;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.IClientLanguage;
import jerozgen.languagereload.helper.ClientLanguageHelper;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.DeprecatedTranslationsInfo;
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
abstract class ClientLanguageMixin extends Language implements IClientLanguage {
    @Unique private static Map<String, Map<String, String>> languagereload_separateTranslationsOnLoad;
    @Unique private final Map<Long, String> languagereload_targetLanguageByThread = Maps.newConcurrentMap();
    @Unique private Map<String, Map<String, String>> languagereload_separateTranslations;

    @Inject(method = "<init>", at = @At("RETURN"))
    void onConstructed(Map<String, String> translations, boolean rightToLeft, CallbackInfo ci) {
        languagereload_separateTranslations = languagereload_separateTranslationsOnLoad;
        languagereload_separateTranslationsOnLoad = null;
    }

    @WrapOperation(method = "loadFrom(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resources/language/ClientLanguage;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/locale/DeprecatedTranslationsInfo;applyToMap(Ljava/util/Map;)V"))
    private static void onLoadFrom$applyDeprecatedTranslationsInfo(DeprecatedTranslationsInfo data, Map<String, String> translations, Operation<Void> applier) {
        applier.call(data, translations);
        for (Map<String, String> map : languagereload_separateTranslationsOnLoad.values()) {
            applier.call(data, map);
        }
    }

    @Inject(method = "loadFrom(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resources/language/ClientLanguage;",
        at = @At("HEAD"))
    private static void onLoadFrom(ResourceManager resourceManager, List<String> filenames, boolean defaultRightToLeft, CallbackInfoReturnable<ClientLanguage> cir) {
        languagereload_separateTranslationsOnLoad = Maps.newHashMap();
    }

    @Redirect(method = "appendFrom", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/locale/Language;loadFromJson(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V"))
    private static void onInternalLoad$saveSeparately(InputStream stream, BiConsumer<String, String> output, String languageName) {
        ClientLanguageHelper.saveSeparately(stream, output, languagereload_separateTranslationsOnLoad, languageName);
    }

    @Override
    public String languagereload_get(String key) {
        var targetLanguage = languagereload_getTargetLanguage();
        if (targetLanguage != null) {
            var targetTranslations = languagereload_separateTranslations.get(targetLanguage);
            return targetTranslations == null ? "" : targetTranslations.getOrDefault(key, "");
        }
        return this.getOrDefault(key);
    }

    @Override
    public String languagereload_getTargetLanguage() {
        return languagereload_targetLanguageByThread.get(Thread.currentThread().threadId());
    }

    @Override
    public void languagereload_setTargetLanguage(@Nullable String value) {
        var threadId = Thread.currentThread().threadId();
        if (value == null)
            languagereload_targetLanguageByThread.remove(threadId);
        else
            languagereload_targetLanguageByThread.put(threadId, value);
    }
}