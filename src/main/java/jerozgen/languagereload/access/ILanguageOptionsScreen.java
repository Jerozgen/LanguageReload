package jerozgen.languagereload.access;

import jerozgen.languagereload.gui.LanguageEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ILanguageOptionsScreen {
    void languagereload_focusEntry(LanguageEntry entry);
}
