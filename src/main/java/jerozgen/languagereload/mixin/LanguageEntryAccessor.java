package jerozgen.languagereload.mixin;

import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.resource.language.LanguageDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry.class)
public interface LanguageEntryAccessor {
    @Accessor
    LanguageDefinition getLanguageDefinition();
}
