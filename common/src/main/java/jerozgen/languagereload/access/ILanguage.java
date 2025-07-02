package jerozgen.languagereload.access;

import net.minecraft.client.resources.language.ClientLanguage;

public interface ILanguage {
    void languagereload_setClientLanguage(ClientLanguage clientLanguage);

    ClientLanguage languagereload_getClientLanguage();
}
