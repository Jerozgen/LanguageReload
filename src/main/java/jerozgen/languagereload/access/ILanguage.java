package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.ClientLanguage;

@Environment(EnvType.CLIENT)
public interface ILanguage {
    void languagereload_setClientLanguage(ClientLanguage clientLanguage);

    ClientLanguage languagereload_getClientLanguage();
}
