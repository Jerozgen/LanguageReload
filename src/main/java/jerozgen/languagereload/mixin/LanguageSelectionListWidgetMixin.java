package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.ILanguageOptionsScreen;
import jerozgen.languagereload.access.ILanguageSelectionListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.class)
public abstract class LanguageSelectionListWidgetMixin
        extends AlwaysSelectedEntryListWidget<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry>
        implements ILanguageSelectionListWidget {
    private List<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> initialChildren;
    private boolean mouseScrolled = false;

    public LanguageSelectionListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        top = 48;
        initialChildren = new ArrayList<>(children());
        if (client.currentScreen instanceof LanguageOptionsScreen languageOptionsScreen) {
            String searchText = ((ILanguageOptionsScreen) languageOptionsScreen).getSearchText();
            if (!searchText.equals("")) {
                filter(searchText);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        mouseScrolled = true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void filter(String searchText) {
        clearEntries();
        for (var languageEntry : initialChildren) {
            var lang = ((LanguageEntryAccessor) languageEntry).getLanguageDefinition();
            String langName = lang.toString().toLowerCase(Locale.ROOT);
            String langCode = lang.getCode().toLowerCase(Locale.ROOT);
            String query = searchText.toLowerCase(Locale.ROOT);
            if (langName.contains(query) || langCode.contains(query)) {
                addEntry(languageEntry);
            }
        }

        if (mouseScrolled) {
            setScrollAmount(getScrollAmount());
        } else if (getSelectedOrNull() != null) {
            centerScrollOn(getSelectedOrNull());
        }
    }
}
