package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ILanguageSelectionListWidget {
    void filter(String searchText);
}
