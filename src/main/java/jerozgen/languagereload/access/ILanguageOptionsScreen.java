package jerozgen.languagereload.access;

import jerozgen.languagereload.gui.LanguageEntry;
import jerozgen.languagereload.gui.LanguageListWidget;

public interface ILanguageOptionsScreen {
    void focusList(LanguageListWidget list);

    void focusEntry(LanguageEntry entry);
}
