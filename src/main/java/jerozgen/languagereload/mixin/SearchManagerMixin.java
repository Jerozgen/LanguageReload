package jerozgen.languagereload.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import jerozgen.languagereload.access.ILanguage;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.search.SearchManager;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SearchManager.class, priority = 990)
abstract class SearchManagerMixin {
    @ModifyExpressionValue(method = "method_60363", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private static String addFallbackTranslationsToSearchTooltips(String original, Text tooltip) {
        if (Config.getInstance() == null) return original;
        if (!Config.getInstance().multilingualItemSearch) return original;

        var translationStorage = ((ILanguage) Language.getInstance()).languagereload_getTranslationStorage();
        if (translationStorage == null) return original;

        var stringBuilder = new StringBuilder(original);
        for (String fallbackCode : Config.getInstance().fallbacks) {
            ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(fallbackCode);
            stringBuilder.append('\n').append(tooltip.getString());
        }

        ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(null);
        return stringBuilder.toString();
    }
}
