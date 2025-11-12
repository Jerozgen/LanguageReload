package jerozgen.languagereload.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
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
    @Shadow private Map<String, LanguageDefinition> languageDefs;

    @Shadow public abstract LanguageDefinition getLanguage(String code);

    @Redirect(method = "reload", at = @At(value = "INVOKE", ordinal = 0, remap = false,
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    boolean onReload$addFallbacks(List<String> list, Object enUsCode) {
        if (Config.getInstance().language.equals(LanguageReload.NO_LANGUAGE)) {
            return true;
        }

        if (languageDefs.isEmpty()) {
            return list.add((String) enUsCode);
        }

        Lists.reverse(Config.getInstance().fallbacks).stream()
                .filter(code -> Objects.nonNull(getLanguage(code)))
                .forEach(list::add);
        return true;
    }

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", remap = false,
            target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    boolean onReload$ignoreNoLanguage(boolean original) {
        return Config.getInstance().language.equals(LanguageReload.NO_LANGUAGE) || languageDefs.isEmpty();
    }

    @Inject(method = "reload", at = @At(value = "INVOKE", ordinal = 0, remap = false,
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    void onReload$setSystemLanguage(ResourceManager manager, CallbackInfo ci) {
        if (LanguageReload.shouldSetSystemLanguage) {
            LanguageReload.shouldSetSystemLanguage = false;
            LanguageReload.LOGGER.info("Language is not set. Setting it to system language");

            var locale = Locale.getDefault();
            var matchingLanguages = languageDefs.keySet().stream()
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
                    .ifPresent(lang -> setSystemLanguage(lang, locale));
            else if (count == 1) setSystemLanguage(matchingLanguages.get(0), locale);
        }
    }

    @Unique
    private static void setSystemLanguage(String lang, Locale locale) {
        LanguageReload.LOGGER.info("Set language to {} (mapped from {})", lang, locale.toLanguageTag());
        LanguageReload.setLanguage(lang);
    }
}
