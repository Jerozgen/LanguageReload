package jerozgen.languagereload.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import jerozgen.languagereload.access.ILanguage;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Consumer;

@Mixin(TranslatableTextContent.class)
abstract class TranslatableTextContentMixin implements TextContent {
    @Shadow @Final private String key;

    @WrapOperation(method = "visit(Lnet/minecraft/text/StringVisitable$Visitor;)Ljava/util/Optional;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/text/TranslatableTextContent;translations:Ljava/util/List;"))
    List<StringVisitable> onVisit(TranslatableTextContent instance, Operation<List<StringVisitable>> translationsGetter) {
        var overriddenTranslations = getOverriddenTranslations();
        if (overriddenTranslations != null) return overriddenTranslations;
        return translationsGetter.call(instance);
    }

    @WrapOperation(method = "visit(Lnet/minecraft/text/StringVisitable$StyledVisitor;Lnet/minecraft/text/Style;)Ljava/util/Optional;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/text/TranslatableTextContent;translations:Ljava/util/List;"))
    List<StringVisitable> onVisitStyled(TranslatableTextContent instance, Operation<List<StringVisitable>> translationsGetter) {
        var overriddenTranslations = getOverriddenTranslations();
        if (overriddenTranslations != null) return overriddenTranslations;
        return translationsGetter.call(instance);
    }

    @Unique
    List<StringVisitable> getOverriddenTranslations() {
        if (!Config.getInstance().multilingualItemSearch) return null;

        var language = Language.getInstance();
        if (language == null) return null;

        var translationStorage = ((ILanguage) language).languagereload_getTranslationStorage();
        if (translationStorage == null) return null;

        var targetLanguage = ((ITranslationStorage) translationStorage).languagereload_getTargetLanguage();
        if (targetLanguage == null) return null;

        var string = ((ITranslationStorage) translationStorage).languagereload_get(key);
        try {
            var builder = new ImmutableList.Builder<StringVisitable>();
            this.forEachPart(string, builder::add);
            return builder.build();
        } catch (TranslationException e) {
            return ImmutableList.of(StringVisitable.plain(string));
        }
    }

    @Shadow protected abstract void forEachPart(String translation, Consumer<StringVisitable> partsConsumer);
}
