package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.ILanguageOptionsScreen;
import jerozgen.languagereload.config.Config;
import jerozgen.languagereload.gui.LanguageEntry;
import jerozgen.languagereload.gui.LanguageListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
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
    @Shadow @Final private static Text LANGUAGE_WARNING_TEXT;

    @Unique private LanguageListWidget availableLanguageList;
    @Unique private LanguageListWidget selectedLanguageList;
    @Unique private TextFieldWidget searchBox;
    @Unique private final LinkedList<String> selectedLanguages = new LinkedList<>();
    @Unique private final Map<String, LanguageEntry> languageEntries = new LinkedHashMap<>();

    LanguageOptionsScreenMixin(Screen parent, GameOptions options, Text title) {
        super(parent, options, title);
    }

    @Unique
    private LanguageOptionsScreen it() {
        return (LanguageOptionsScreen) (Object) this;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Screen parent, GameOptions options, LanguageManager languageManager, CallbackInfo ci) {
        var currentLangCode = languageManager.getLanguage().getCode();
        if (!currentLangCode.equals(LanguageReload.NO_LANGUAGE_CODE))
            selectedLanguages.add(currentLangCode);
        selectedLanguages.addAll(Config.getInstance().fallbacks);
        for (var language : languageManager.getAllLanguages()) {
            var code = language.getCode();
            languageEntries.put(code, new LanguageEntry(this::refresh, code, language, selectedLanguages));
        }
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    void onInit(CallbackInfo ci) {
        searchBox = new TextFieldWidget(textRenderer, width / 2 - 100, 22, 200, 20, searchBox, Text.empty()) {
            @Override
            public void setFocused(boolean focused) {
                if (!isFocused() && focused) {
                    super.setFocused(true);
                    focusSearch();
                }
                else super.setFocused(focused);
            }
        };
        searchBox.setChangedListener(__ -> refresh());
        addSelectableChild(searchBox);

        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList = new LanguageListWidget(client, it(), listWidth, height, Text.translatable("pack.available.title"));
        selectedLanguageList = new LanguageListWidget(client, it(), listWidth, height, Text.translatable("pack.selected.title"));
        availableLanguageList.setLeftPos(width / 2 - 4 - listWidth);
        selectedLanguageList.setLeftPos(width / 2 + 4);
        addSelectableChild(availableLanguageList);
        addSelectableChild(selectedLanguageList);
        refresh();

        addDrawableChild(gameOptions.getForceUnicodeFont().createButton(gameOptions, width / 2 - 155, height - 28, 150));
        addDrawableChild(new ButtonWidget(width / 2 - 155 + 160, height - 28, 150, 20, ScreenTexts.DONE, this::onDone));
        setInitialFocus(searchBox);

        super.init();
        ci.cancel();
    }

    @Unique
    private void onDone(ButtonWidget button) {
        if (client == null) return;
        client.setScreen(parent);

        var language = selectedLanguages.peekFirst();
        if (language == null) {
            LanguageReload.setLanguage(LanguageReload.NO_LANGUAGE_CODE, new LinkedList<>());
        } else {
            var fallbacks = new LinkedList<>(selectedLanguages);
            fallbacks.removeFirst();
            LanguageReload.setLanguage(language, fallbacks);
        }
    }

    @Override
    public void languagereload_focusEntry(LanguageEntry entry) {
        ((AlwaysSelectedEntryListWidgetAccessor) availableLanguageList).setInFocus(false);
        ((AlwaysSelectedEntryListWidgetAccessor) selectedLanguageList).setInFocus(false);
        entry.getParent().setSelected(entry);
        entry.getParent().changeFocus(true);
        setFocused(entry.getParent());
    }

    @Unique
    private void focusSearch() {
        ((AlwaysSelectedEntryListWidgetAccessor) availableLanguageList).setInFocus(false);
        ((AlwaysSelectedEntryListWidgetAccessor) selectedLanguageList).setInFocus(false);
        setFocused(searchBox);
    }

    @Unique
    private void refresh() {
        refreshList(selectedLanguageList, selectedLanguages.stream().map(languageEntries::get).filter(Objects::nonNull));
        refreshList(availableLanguageList, languageEntries.values().stream()
                .filter(entry -> {
                    if (selectedLanguageList.children().contains(entry)) return false;
                    var query = searchBox.getText().toLowerCase(Locale.ROOT);
                    var langCode = entry.getCode().toLowerCase(Locale.ROOT);
                    var langName = entry.getLanguage().toString().toLowerCase(Locale.ROOT);
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
        list.setScrollAmount(list.getScrollAmount());
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderBackgroundTexture(0);

        availableLanguageList.render(matrices, mouseX, mouseY, delta);
        selectedLanguageList.render(matrices, mouseX, mouseY, delta);
        searchBox.render(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, textRenderer, title, width / 2, 8, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, LANGUAGE_WARNING_TEXT, width / 2, height - 46, 0x808080);

        super.render(matrices, mouseX, mouseY, delta);
        ci.cancel();
    }

    @Override
    public void tick() {
        searchBox.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) || searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return searchBox.charTyped(chr, modifiers);
    }
}
