package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.TranslationStorage;

@Environment(EnvType.CLIENT)
public interface ILanguage {
    void languagereload_setTranslationStorage(TranslationStorage translationStorage);

    TranslationStorage languagereload_getTranslationStorage();
}
