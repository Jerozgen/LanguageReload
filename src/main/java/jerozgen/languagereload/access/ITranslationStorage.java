package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface ITranslationStorage {
    @Nullable String getTargetLanguage();

    void setTargetLanguage(@Nullable String value);
}
