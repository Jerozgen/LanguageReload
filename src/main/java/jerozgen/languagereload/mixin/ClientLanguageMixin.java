package jerozgen.languagereload.mixin;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import jerozgen.languagereload.access.IClientLanguage;
import jerozgen.languagereload.config.Config;
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
public abstract class ClientLanguageMixin extends Language implements IClientLanguage {
    @Unique private final Map<Long, String> targetLanguageByThread = Maps.newConcurrentMap();
    @Unique private static Map<String, Map<String, String>> storagesOnLoad;
    @Unique private Map<String, Map<String, String>> storages;

    @Inject(method = "<init>", at = @At("RETURN"))
    void onConstructed(Map<String, String> storage, boolean defaultRightToLeft, CallbackInfo ci) {
        storages = storagesOnLoad;
        storagesOnLoad = null;
    }

    @WrapOperation(method = "loadFrom(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resources/language/ClientLanguage;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/locale/DeprecatedTranslationsInfo;applyToMap(Ljava/util/Map;)V"))
    private static void onLoadFrom$applyDeprecatedLanguageData(DeprecatedTranslationsInfo deprecatedInfo, Map<String, String> translations, Operation<Void> applier) {
        applier.call(deprecatedInfo, translations);
        for (Map<String, String> storage : storagesOnLoad.values()) {
            applier.call(deprecatedInfo, storage);
        }
    }

    @Inject(method = "loadFrom(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resources/language/ClientLanguage;",
            at = @At("HEAD"))
    private static void onLoadFrom(ResourceManager resourceManager, List<String> languageStack, boolean defaultRightToLeft, CallbackInfoReturnable<ClientLanguage> cir) {
        storagesOnLoad = Maps.newHashMap();
    }

    @Redirect(method = "appendFrom(Ljava/lang/String;Ljava/util/List;Ljava/util/Map;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/locale/Language;loadFromJson(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V"))
    private static void onAppendFrom$saveSeparately(InputStream inputStream, BiConsumer<String, String> entryConsumer, String languageCode) {
        if (Config.getInstance().multilingualItemSearch) {
            Language.loadFromJson(inputStream, entryConsumer.andThen((key, value) ->
                    storagesOnLoad.computeIfAbsent(languageCode, k -> Maps.newHashMap()).put(key, value)));
        } else Language.loadFromJson(inputStream, entryConsumer);
    }

    @Override
    public String languagereload_get(String key) {
        var targetLanguage = languagereload_getTargetLanguage();
        if (targetLanguage != null) {
            var targetStorage = storages.get(targetLanguage);
            return targetStorage == null ? "" : targetStorage.getOrDefault(key, "");
        }
        return this.getOrDefault(key);
    }

    @Override
    public @Nullable String languagereload_getTargetLanguage() {
        return targetLanguageByThread.get(Thread.currentThread().threadId());
    }

    @Override
    public void languagereload_setTargetLanguage(@Nullable String value) {
        var threadId = Thread.currentThread().threadId();
        if (value == null) targetLanguageByThread.remove(threadId);
        else targetLanguageByThread.put(threadId, value);
    }
}
