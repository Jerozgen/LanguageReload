package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.ILanguageOptionsScreen;
import jerozgen.languagereload.gui.LanguageEntry;
import jerozgen.languagereload.gui.LanguageListWidget;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Stream;

@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen implements ILanguageOptionsScreen {
    @Unique private LanguageListWidget availableLanguageList;
    @Unique private LanguageListWidget selectedLanguageList;
    @Unique private TextFieldWidget searchBox;
    @Unique private final LinkedList<String> selectedLanguages = new LinkedList<>();
    @Unique private final Map<String, LanguageEntry> languageEntries = new LinkedHashMap<>();

    @Shadow private LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionList;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Screen parent, GameOptions options, LanguageManager languageManager, CallbackInfo ci) {
        selectedLanguages.addAll(LanguageReload.getLanguages());

        var languages = languageManager.getAllLanguages();
        if (languages.isEmpty()) {
            var defaultLanguage = LanguageManagerAccessor.languagereload_getEnglishUs();
            languageEntries.put(Language.DEFAULT_LANGUAGE, new LanguageEntry(this::refresh, Language.DEFAULT_LANGUAGE, defaultLanguage, selectedLanguages));
        } else {
            languages.forEach((code, language) -> languageEntries.put(code, new LanguageEntry(this::refresh, code, language, selectedLanguages)));
        }

        layout.setHeaderHeight(48);
        layout.setFooterHeight(53);
    }

    @Inject(method = "initBody", at = @At("HEAD"), cancellable = true)
    void onInitBody(CallbackInfo ci) {
        languageSelectionList = LanguageSelectionListWidgetAccessor.languagereload_init(it(), client);

        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList = new LanguageListWidget(client, it(), listWidth, height, Text.translatable("pack.available.title"));
        selectedLanguageList = new LanguageListWidget(client, it(), listWidth, height, Text.translatable("pack.selected.title"));
        availableLanguageList.setX(width / 2 - 4 - listWidth);
        selectedLanguageList.setX(width / 2 + 4);
        layout.addBody(availableLanguageList);
        layout.addBody(selectedLanguageList);
        refresh();

        ci.cancel();
    }

    @Override
    protected void initHeader() {
        searchBox = new TextFieldWidget(textRenderer, width / 2 - 100, 22, 200, 20, searchBox, Text.empty()) {
            @Override
            public void setFocused(boolean focused) {
                if (!isFocused() && focused) {
                    super.setFocused(true);
                    focusSearch();
                } else super.setFocused(focused);
            }
        };
        searchBox.setChangedListener(__ -> refresh());

        var header = layout.addHeader(DirectionalLayoutWidget.vertical().spacing(5));
        header.getMainPositioner().alignHorizontalCenter();
        header.add(new TextWidget(title, textRenderer));
        header.add(searchBox);
    }

    @Inject(method = "initTabNavigation", at = @At("HEAD"), cancellable = true)
    protected void onInitTabNavigation(CallbackInfo ci) {
        super.initTabNavigation();

        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList.position(listWidth, layout);
        selectedLanguageList.position(listWidth, layout);
        availableLanguageList.setX(width / 2 - 4 - listWidth);
        selectedLanguageList.setX(width / 2 + 4);
        availableLanguageList.updateScroll();
        selectedLanguageList.updateScroll();

        ci.cancel();
    }

    @Inject(method = "onDone", at = @At("HEAD"), cancellable = true)
    private void onDone(CallbackInfo ci) {
        if (client == null) return;
        client.setScreen(parent);

        var language = selectedLanguages.peekFirst();
        if (language == null) {
            LanguageReload.setLanguage(null);
        } else {
            var fallbacks = new LinkedList<>(selectedLanguages);
            fallbacks.removeFirst();
            LanguageReload.setLanguage(language, fallbacks);
        }

        ci.cancel();
    }

    @Unique
    private void refresh() {
        refreshList(selectedLanguageList, selectedLanguages.stream().map(languageEntries::get).filter(Objects::nonNull));
        refreshList(availableLanguageList, languageEntries.values().stream()
                .filter(entry -> {
                    if (selectedLanguageList.children().contains(entry)) return false;
                    var query = searchBox.getText().toLowerCase(Locale.ROOT);
                    var langCode = entry.getCode().toLowerCase(Locale.ROOT);
                    var langName = entry.getLanguage().getDisplayText().getString().toLowerCase(Locale.ROOT);
                    return langCode.contains(query) || langName.contains(query);
                }));
    }

    @Unique
    private void refreshList(LanguageListWidget list, Stream<? extends LanguageEntry> entries) {
        var selectedEntry = list.getSelectedOrNull();
        list.setSelected(null);
        list.children().clear();
        entries.forEach(entry -> {
            list.children().add(entry);
            entry.setParent(list);
            if (entry == selectedEntry) {
                list.setSelected(entry);
            }
        });
        list.updateScroll();
    }

    @Override
    protected void setInitialFocus() {
        focusSearch();
    }

    @Unique
    private void focusSearch() {
        switchFocus(GuiNavigationPath.of(searchBox, this));
    }

    @Override
    public void languagereload_focusList(LanguageListWidget list) {
        switchFocus(GuiNavigationPath.of(list, this));
    }

    @Override
    public void languagereload_focusEntry(LanguageEntry entry) {
        switchFocus(GuiNavigationPath.of(entry, entry.getParent(), this));
    }

    @Unique
    LanguageOptionsScreen it() {
        return (LanguageOptionsScreen) (Object) this;
    }

    LanguageOptionsScreenMixin(Screen parent, GameOptions options, Text title) {
        super(parent, options, title);
    }
}
