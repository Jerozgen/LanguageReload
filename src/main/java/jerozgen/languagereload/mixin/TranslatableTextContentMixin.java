package jerozgen.languagereload.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import jerozgen.languagereload.access.ILanguage;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin(TranslatableTextContent.class)
abstract class TranslatableTextContentMixin implements TextContent {
    @Unique private final Map<Long, String> previousTargetLanguageByThread = new HashMap<>();
    @Unique private final Map<String, List<StringVisitable>> separateTranslationsCache = Maps.newHashMap();

    @Shadow @Final private String key;
    @Shadow private @Nullable Language languageCache;
    @Shadow private List<StringVisitable> translations;

    @Inject(method = "updateTranslations", at = @At("RETURN"))
    void onUpdateTranslations(CallbackInfo ci) {
        if (Config.getInstance() == null) return;
        if (!Config.getInstance().multilingualItemSearch) return;
        if (languageCache == null) return;

        var translationStorage = ((ILanguage) languageCache).languagereload_getTranslationStorage();
        if (translationStorage == null) return;

        var targetLanguage = ((ITranslationStorage) translationStorage).languagereload_getTargetLanguage();
        if (Objects.equals(getPreviousTargetLanguage(), targetLanguage)) return;
        setPreviousTargetLanguage(targetLanguage);

        if (targetLanguage == null) {
            separateTranslationsCache.clear();
            translations = ImmutableList.of();
            languageCache = null;
            return;
        }

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

    @Unique
    public @Nullable String getPreviousTargetLanguage() {
        return previousTargetLanguageByThread.get(Thread.currentThread().threadId());
    }

    @Unique
    public void setPreviousTargetLanguage(@Nullable String value) {
        previousTargetLanguageByThread.put(Thread.currentThread().threadId(), value);
    }

    @Shadow protected abstract void forEachPart(String translation, Consumer<StringVisitable> partsConsumer);
}
