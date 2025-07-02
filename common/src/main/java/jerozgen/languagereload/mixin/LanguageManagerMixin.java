package jerozgen.languagereload.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(LanguageManager.class)
abstract class LanguageManagerMixin {
    @Shadow private Map<String, LanguageInfo> languages;

    @Unique
    private static void languagereload_setSystemLanguage(String lang, Locale locale) {
        LanguageReload.LOGGER.info("Set language to {} (mapped from {})", lang, locale.toLanguageTag());
        LanguageReload.setLanguage(lang, new LinkedList<>() {{
            if (!lang.equals(Language.DEFAULT))
                add(Language.DEFAULT);
        }});
    }

    @Shadow public abstract LanguageInfo getLanguage(String code);

    @Redirect(method = "onResourceManagerReload", at = @At(value = "INVOKE", ordinal = 0, remap = false,
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    boolean onResourceManagerReload$addFallbacks(List<String> list, Object enUsCode) {
        Lists.reverse(Config.getInstance().fallbacks).stream()
                .filter(code -> Objects.nonNull(getLanguage(code)))
                .forEach(list::add);
        return true;
    }

    @ModifyExpressionValue(method = "onResourceManagerReload", at = @At(value = "INVOKE", remap = false,
            target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    boolean onResourceManagerReload$ignoreNoLanguage(boolean original) {
        return Config.getInstance().language.equals(LanguageReload.NO_LANGUAGE);
    }

    @Inject(method = "onResourceManagerReload", at = @At(value = "INVOKE", ordinal = 0, remap = false,
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    void onResourceManagerReload$setSystemLanguage(ResourceManager manager, CallbackInfo ci) {
        if (LanguageReload.shouldSetSystemLanguage) {
            LanguageReload.shouldSetSystemLanguage = false;
            LanguageReload.LOGGER.info("Language is not set. Setting it to system language");

            var locale = Locale.getDefault();
            var matchingLanguages = languages.keySet().stream()
                    .filter(code -> code.split("_")[0].equalsIgnoreCase(locale.getLanguage()))
                    .toList();
            var count = matchingLanguages.size();
            if (count > 1) matchingLanguages.stream()
                    .filter(code -> {
                        var split = code.split("_");
                        if (split.length < 2) return false;
                        return split[1].equalsIgnoreCase(locale.getCountry());
                    })
                    .findFirst()
                    .ifPresent(lang -> languagereload_setSystemLanguage(lang, locale));
            else if (count == 1) languagereload_setSystemLanguage(matchingLanguages.getFirst(), locale);
        }
    }
}
