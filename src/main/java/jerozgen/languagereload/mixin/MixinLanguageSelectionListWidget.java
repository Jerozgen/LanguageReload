package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.ILanguageEntry;
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
public abstract class MixinLanguageSelectionListWidget
        extends AlwaysSelectedEntryListWidget<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry>
        implements ILanguageSelectionListWidget {
    private List<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> initialChildren;
    private boolean mouseScrolled = false;

    public MixinLanguageSelectionListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void onConstructed(CallbackInfo ci) {
        this.top = 48;
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
        for (LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry entry : initialChildren) {
            LanguageDefinition lang = ((ILanguageEntry) entry).getLanguageDefinition();
            String query = searchText.toLowerCase(Locale.ROOT);
            if (lang.toString().toLowerCase(Locale.ROOT).contains(query) || lang.getCode().toLowerCase(Locale.ROOT).contains(query)) {
                addEntry(entry);
            }
        }

        if (mouseScrolled) {
            setScrollAmount(getScrollAmount());
        } else if (getSelectedOrNull() != null) {
            centerScrollOn(getSelectedOrNull());
        }
    }
}
