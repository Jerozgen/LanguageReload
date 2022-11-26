package jerozgen.languagereload.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin(TranslatableTextContent.class)
abstract class TranslatableTextContentMixin implements TextContent {
    private @Nullable String previousTargetLanguage;
    private final Map<String, List<StringVisitable>> separateTranslationsCache = Maps.newHashMap();
    private @Nullable List<StringVisitable> savedTranslations;

    @Shadow @Final private String key;
    @Shadow private @Nullable Language languageCache;
    @Shadow private List<StringVisitable> translations;

    @Inject(method = "updateTranslations", at = @At("RETURN"))
    void onUpdateTranslations(CallbackInfo ci) {
        if (Config.getInstance() == null) return;
        if (!Config.getInstance().multilingualItemSearch) return;
        if (!(languageCache instanceof TranslationStorage)) return;

        var targetLanguage = ((ITranslationStorage) languageCache).getTargetLanguage();
        if (Objects.equals(previousTargetLanguage, targetLanguage)) return;

        if (targetLanguage == null) {
            previousTargetLanguage = null;
            translations = savedTranslations;
            savedTranslations = null;
            return;
        }

        if (previousTargetLanguage == null) {
            savedTranslations = translations;
        }
        previousTargetLanguage = targetLanguage;
        translations = separateTranslationsCache.computeIfAbsent(targetLanguage, k -> {
            var string = languageCache.get(key);
            try {
                var builder = new ImmutableList.Builder<StringVisitable>();
                this.forEachPart(string, builder::add);
                return builder.build();
            } catch (TranslationException e) {
                return ImmutableList.of(StringVisitable.plain(string));
            }
        });
    }

    @Inject(method = "updateTranslations", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/Language;get(Ljava/lang/String;)Ljava/lang/String;"))
    void onUpdateTranslations$clearCache(CallbackInfo ci) {
        previousTargetLanguage = null;
        separateTranslationsCache.clear();
        savedTranslations = null;
    }

    @Shadow protected abstract void forEachPart(String translation, Consumer<StringVisitable> partsConsumer);
}
