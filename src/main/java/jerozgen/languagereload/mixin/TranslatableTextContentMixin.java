package jerozgen.languagereload.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Mixin(TranslatableComponent.class)
abstract class TranslatableTextContentMixin implements MutableComponent {
    @Unique private @Nullable String previousTargetLanguage;
    @Unique private final Map<String, List<FormattedText>> separateTranslationsCache = Maps.newHashMap();
    @Unique private @Nullable List<FormattedText> savedTranslations;

    @Shadow @Final private String key;
    @Shadow private @Nullable Language decomposedWith;
    @Shadow private List<FormattedText> decomposedParts;

    @Inject(method = "decompose", at = @At("RETURN"))
    void onUpdateTranslations(CallbackInfo ci) {
        if (Config.getInstance() == null) return;
        if (!Config.getInstance().multilingualItemSearch) return;
        if (!(decomposedWith instanceof ClientLanguage)) return;

        var targetLanguage = ((ITranslationStorage) decomposedWith).languagereload_getTargetLanguage();
        if (Objects.equals(previousTargetLanguage, targetLanguage)) return;

        if (targetLanguage == null) {
            previousTargetLanguage = null;
            decomposedParts = savedTranslations;
            savedTranslations = null;
            return;
        }

        if (previousTargetLanguage == null) {
            savedTranslations = decomposedParts;
        }
        previousTargetLanguage = targetLanguage;
        decomposedParts = separateTranslationsCache.computeIfAbsent(targetLanguage, k -> {
            var string = decomposedWith.getOrDefault(key);
            try {
                var builder = new ImmutableList.Builder<FormattedText>();
                this.decomposeTemplate(string, builder::add);
                return builder.build();
            } catch (TranslatableFormatException e) {
                return ImmutableList.of(FormattedText.of(string));
            }
        });
    }

    @Inject(method = "decompose", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/locale/Language;getOrDefault(Ljava/lang/String;)Ljava/lang/String;"))
    void onUpdateTranslations$clearCache(CallbackInfo ci) {
        previousTargetLanguage = null;
        separateTranslationsCache.clear();
        savedTranslations = null;
    }

    @Shadow protected abstract void decomposeTemplate(String translation, Consumer<FormattedText> partsConsumer);
}