package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.ILanguageSelectScreen;
import jerozgen.languagereload.config.Config;
import jerozgen.languagereload.gui.LanguageEntry;
import jerozgen.languagereload.gui.LanguageListWidget;
import net.minecraft.client.Options;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Stream;

@Mixin(LanguageSelectScreen.class)
public abstract class LanguageSelectScreenMixin extends OptionsSubScreen implements ILanguageSelectScreen {
    @Unique private final LinkedList<String> languagereload_selectedLanguages = new LinkedList<>();
    @Unique private final Map<String, LanguageEntry> languagereload_languageEntries = new LinkedHashMap<>();
    @Unique private LanguageListWidget languagereload_availableLanguageList;
    @Unique private LanguageListWidget languagereload_selectedLanguageList;
    @Unique private EditBox languagereload_searchBox;
    @Shadow private LanguageSelectScreen.LanguageSelectionList languageSelectionList;

    LanguageSelectScreenMixin(Screen parent, Options options, Component title) {
        super(parent, options, title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Screen parent, Options options, LanguageManager languageManager, CallbackInfo ci) {
        var currentLangCode = languageManager.getSelected();
        if (!currentLangCode.equals(LanguageReload.NO_LANGUAGE))
            languagereload_selectedLanguages.add(currentLangCode);
        languagereload_selectedLanguages.addAll(Config.getInstance().fallbacks);
        languageManager.getLanguages().forEach((code, language) ->
                languagereload_languageEntries.put(code, new LanguageEntry(this::languagereload_refresh, code, language, languagereload_selectedLanguages)));

        layout.setHeaderHeight(48);
        layout.setFooterHeight(53);
    }

    @Inject(method = "addContents", at = @At("HEAD"), cancellable = true)
    void onAddContents(CallbackInfo ci) {
        languageSelectionList = LanguageSelectionListWidgetAccessor.languagereload_init(languagereload_it(), minecraft);

        var listWidth = Math.min(width / 2 - 4, 200);
        languagereload_availableLanguageList = new LanguageListWidget(minecraft, languagereload_it(), listWidth, height, Component.translatable("pack.available.title"));
        languagereload_selectedLanguageList = new LanguageListWidget(minecraft, languagereload_it(), listWidth, height, Component.translatable("pack.selected.title"));
        languagereload_availableLanguageList.setX(width / 2 - 4 - listWidth);
        languagereload_selectedLanguageList.setX(width / 2 + 4);
        layout.addToContents(languagereload_availableLanguageList);
        layout.addToContents(languagereload_selectedLanguageList);
        languagereload_refresh();

        ci.cancel();
    }

    @Override
    protected void addTitle() {
        languagereload_searchBox = new EditBox(font, width / 2 - 100, 22, 200, 20, languagereload_searchBox, Component.empty()) {
            @Override
            public void setFocused(boolean focused) {
                if (!isFocused() && focused) {
                    super.setFocused(true);
                    languagereload_focusSearch();
                } else super.setFocused(focused);
            }
        };
        languagereload_searchBox.setResponder(__ -> languagereload_refresh());

        var header = layout.addToHeader(LinearLayout.vertical().spacing(5));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(title, font));
        header.addChild(languagereload_searchBox);
    }

    @Inject(method = "repositionElements", at = @At("HEAD"), cancellable = true)
    protected void onRepositionElements(CallbackInfo ci) {
        super.repositionElements();

        var listWidth = Math.min(width / 2 - 4, 200);
        languagereload_availableLanguageList.updateSize(listWidth, layout);
        languagereload_selectedLanguageList.updateSize(listWidth, layout);
        languagereload_availableLanguageList.setX(width / 2 - 4 - listWidth);
        languagereload_selectedLanguageList.setX(width / 2 + 4);
        languagereload_availableLanguageList.refreshScrollAmount();
        languagereload_selectedLanguageList.refreshScrollAmount();

        ci.cancel();
    }

    @Inject(method = "onDone", at = @At("HEAD"), cancellable = true)
    private void onDone(CallbackInfo ci) {
        if (minecraft == null) return;
        minecraft.setScreen(lastScreen);

        var language = languagereload_selectedLanguages.peekFirst();
        if (language == null) {
            LanguageReload.setLanguage(LanguageReload.NO_LANGUAGE, new LinkedList<>());
        } else {
            var fallbacks = new LinkedList<>(languagereload_selectedLanguages);
            fallbacks.removeFirst();
            LanguageReload.setLanguage(language, fallbacks);
        }

        ci.cancel();
    }

    @Unique
    private void languagereload_refresh() {
        languagereload_refreshList(languagereload_selectedLanguageList, languagereload_selectedLanguages.stream().map(languagereload_languageEntries::get).filter(Objects::nonNull));
        languagereload_refreshList(languagereload_availableLanguageList, languagereload_languageEntries.values().stream()
                .filter(entry -> {
                    if (languagereload_selectedLanguageList.children().contains(entry)) return false;
                    var query = languagereload_searchBox.getValue().toLowerCase(Locale.ROOT);
                    var langCode = entry.getCode().toLowerCase(Locale.ROOT);
                    var langName = entry.getLanguage().toComponent().getString().toLowerCase(Locale.ROOT);
                    return langCode.contains(query) || langName.contains(query);
                }));
    }

    @Unique
    private void languagereload_refreshList(LanguageListWidget list, Stream<? extends LanguageEntry> entries) {
        var selectedEntry = list.getSelected();
        list.setSelected(null);
        list.children().clear();
        entries.forEach(entry -> {
            list.children().add(entry);
            entry.setParent(list);
            if (entry == selectedEntry) {
                list.setSelected(entry);
            }
        });
        list.refreshScrollAmount();
    }

    @Override
    protected void setInitialFocus() {
        languagereload_focusSearch();
    }

    @Unique
    private void languagereload_focusSearch() {
        changeFocus(ComponentPath.path(languagereload_searchBox, this));
    }

    @Override
    public void languagereload_focusList(LanguageListWidget list) {
        changeFocus(ComponentPath.path(list, this));
    }

    @Override
    public void languagereload_focusEntry(LanguageEntry entry) {
        changeFocus(ComponentPath.path(entry, entry.getParent(), this));
    }

    @Unique
    LanguageSelectScreen languagereload_it() {
        return (LanguageSelectScreen) (Object) this;
    }
}
