package jerozgen.languagereload.mixin;

import com.google.common.collect.Lists;
import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(LanguageManager.class)
abstract class LanguageManagerMixin {
    @Shadow private Map<String, LanguageDefinition> languageDefs;
    @Shadow private LanguageDefinition language;

    @Shadow public abstract LanguageDefinition getLanguage(String code);

    @Shadow private String currentLanguageCode;

    @Redirect(method = "reload", at = @At(value = "INVOKE", remap = false,
            target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;"))
    ArrayList<LanguageDefinition> onReload$onCreateList(Object[] elements) {
        var noLanguage = currentLanguageCode.equals(LanguageReload.NO_LANGUAGE_CODE);
        if (noLanguage)
            language = LanguageReload.NO_LANGUAGE;

        var list = Lists.reverse(Config.getInstance().fallbacks).stream()
                .map(this::getLanguage)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(Lists::newArrayList));

        var currentLanguage = this.getLanguage(currentLanguageCode);
        if (!noLanguage && currentLanguage != null)
            list.add(currentLanguage);

        return list;
    }

    @Redirect(method = "reload", at = @At(value = "INVOKE", remap = false,
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    boolean onReload$onAddToList(List<Object> list, Object language) {
        return true;
    }

    @Inject(method = "reload", at = @At(value = "INVOKE", ordinal = 0, remap = false,
            target = "Ljava/util/Map;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
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
        LanguageReload.setLanguage(lang, new LinkedList<>() {{
            if (!lang.equals(Language.DEFAULT_LANGUAGE))
                add(Language.DEFAULT_LANGUAGE);
        }});
    }
}
