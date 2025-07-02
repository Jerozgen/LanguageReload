package jerozgen.languagereload.access;

import jerozgen.languagereload.gui.LanguageEntry;
import jerozgen.languagereload.gui.LanguageListWidget;

public interface ILanguageSelectScreen {
    void languagereload_focusList(LanguageListWidget list);

    void languagereload_focusEntry(LanguageEntry entry);
}
