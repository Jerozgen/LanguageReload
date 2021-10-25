package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.ILanguageEntry;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry.class)
public abstract class MixinLanguageEntry
        extends AlwaysSelectedEntryListWidget.Entry<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry>
        implements ILanguageEntry {
    @Shadow @Final LanguageDefinition languageDefinition;

    @Override
    public LanguageDefinition getLanguageDefinition() {
        return languageDefinition;
    }
}
