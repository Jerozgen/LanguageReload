package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.ILanguageSelectScreen;
import jerozgen.languagereload.gui.LanguageEntry;
import jerozgen.languagereload.gui.LanguageList;
import net.minecraft.client.Options;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Mixin(LanguageSelectScreen.class)
public abstract class LanguageSelectScreenMixin extends OptionsSubScreen implements ILanguageSelectScreen {
    @Unique private LanguageList availableLanguageList;
    @Unique private LanguageList selectedLanguageList;
    @Unique private EditBox search;
    @Unique private final LinkedList<String> selectedLanguages = new LinkedList<>();
    @Unique private final Map<String, LanguageEntry> languageEntries = new LinkedHashMap<>();

    @Shadow @Final private static Component SEARCH_HINT;
    @Shadow private LanguageSelectScreen.LanguageSelectionList languageSelectionList;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Screen lastScreen, Options options, LanguageManager languageManager, CallbackInfo ci) {
        selectedLanguages.addAll(LanguageReload.getLanguages());

        var languages = languageManager.getLanguages();
        if (languages.isEmpty()) {
            var defaultLanguage = LanguageManagerAccessor.languagereload_getDefaultLanguage();
            languageEntries.put(Language.DEFAULT, new LanguageEntry(this::refresh, Language.DEFAULT, defaultLanguage, selectedLanguages));
        } else {
            languages.forEach((code, language) -> languageEntries.put(code, new LanguageEntry(this::refresh, code, language, selectedLanguages)));
        }

        layout.setHeaderHeight(48);
        layout.setFooterHeight(53);
    }

    @Inject(method = "addContents", at = @At("HEAD"), cancellable = true)
    void onAddContents(CallbackInfo ci) {
        languageSelectionList = LanguageSelectionListAccessor.languagereload_init(it(), minecraft);

        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList = new LanguageList(minecraft, it(), listWidth, height, Component.translatable("pack.available.title"));
        selectedLanguageList = new LanguageList(minecraft, it(), listWidth, height, Component.translatable("pack.selected.title"));
        availableLanguageList.setX(width / 2 - 4 - listWidth);
        selectedLanguageList.setX(width / 2 + 4);
        layout.addToContents(availableLanguageList);
        layout.addToContents(selectedLanguageList);
        refresh();

        ci.cancel();
    }

    @Override
    public void addTitle() {
        search = new EditBox(font, width / 2 - 100, 22, 200, 20, search, Component.empty()) {
            @Override
            public void setFocused(boolean focused) {
                if (!isFocused() && focused) {
                    super.setFocused(true);
                    focusSearch();
                } else super.setFocused(focused);
            }
        };
        search.setHint(SEARCH_HINT);
        search.setResponder(__ -> refresh());

        var header = layout.addToHeader(LinearLayout.vertical().spacing(5));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(title, font));
        header.addChild(search);
    }

    @Inject(method = "repositionElements", at = @At("HEAD"), cancellable = true)
    protected void onRepositionElements(CallbackInfo ci) {
        super.repositionElements();

        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList.updateSize(listWidth, layout);
        selectedLanguageList.updateSize(listWidth, layout);
        availableLanguageList.setX(width / 2 - 4 - listWidth);
        selectedLanguageList.setX(width / 2 + 4);
        availableLanguageList.refreshScrollAmount();
        selectedLanguageList.refreshScrollAmount();

        ci.cancel();
    }

    @Inject(method = "onDone", at = @At("HEAD"), cancellable = true)
    private void onDone(CallbackInfo ci) {
        if (minecraft == null) return;
        minecraft.setScreen(lastScreen);

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
        selectedLanguageList.set(selectedLanguages.stream().map(languageEntries::get).filter(Objects::nonNull));
        availableLanguageList.set(languageEntries.values().stream()
                .filter(entry -> {
                    if (selectedLanguageList.children().contains(entry)) return false;
                    var query = search.getValue().toLowerCase(Locale.ROOT);
                    var langCode = entry.getCode().toLowerCase(Locale.ROOT);
                    var langName = entry.getLanguage().toComponent().getString().toLowerCase(Locale.ROOT);
                    return langCode.contains(query) || langName.contains(query);
                }));
    }

    @Override
    public void setInitialFocus() {
        focusSearch();
    }

    @Unique
    private void focusSearch() {
        changeFocus(ComponentPath.path(search, this));
    }

    @Override
    public void languagereload_focusEntry(LanguageEntry entry) {
        changeFocus(ComponentPath.path(entry, entry.getParent(), this));
    }

    @Unique
    LanguageSelectScreen it() {
        return (LanguageSelectScreen) (Object) this;
    }

    LanguageSelectScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }
}
