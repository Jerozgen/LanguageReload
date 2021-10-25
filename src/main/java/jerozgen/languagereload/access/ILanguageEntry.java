package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;

@Environment(EnvType.CLIENT)
public interface ILanguageEntry {
    LanguageDefinition getLanguageDefinition();
}
