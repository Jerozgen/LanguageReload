package jerozgen.languagereload.helper;

import com.google.common.collect.Maps;
import jerozgen.languagereload.config.Config;
import net.minecraft.locale.Language;

import java.io.InputStream;
import java.util.Map;
import java.util.function.BiConsumer;

public class ClientLanguageHelper {
    public static void saveSeparately(InputStream stream, BiConsumer<String, String> output, Map<String, Map<String, String>> separateTranslationsOnLoad, String languageName) {
        if (Config.getInstance().multilingualItemSearch) {
            Language.loadFromJson(stream, output.andThen((key, value) ->
                separateTranslationsOnLoad.computeIfAbsent(languageName, k -> Maps.newHashMap()).put(key, value)));
        } else {
            Language.loadFromJson(stream, output);
        }
    }
}
