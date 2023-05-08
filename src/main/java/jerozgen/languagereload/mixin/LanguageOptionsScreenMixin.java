package jerozgen.languagereload.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.ILanguageOptionsScreen;
import jerozgen.languagereload.config.Config;
import jerozgen.languagereload.gui.*;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Stream;

@Mixin(LanguageSelectScreen.class)
public abstract class LanguageOptionsScreenMixin extends OptionsSubScreen implements ILanguageOptionsScreen {
    @Shadow @Final private static Component WARNING_LABEL;

    @Unique private LanguageListWidget availableLanguageList;
    @Unique private LanguageListWidget selectedLanguageList;
    @Unique private EditBox searchBox;
    @Unique private final LinkedList<String> selectedLanguages = new LinkedList<>();
    @Unique private final Map<String, MovableLanguageEntry> languageEntries = new LinkedHashMap<>();
    @Unique private LockedLanguageEntry defaultLanguageEntry;

    LanguageOptionsScreenMixin(Screen parent, Options options, Component title) {
        super(parent, options, title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Screen parent, Options options, LanguageManager languageManager, CallbackInfo ci) {
        var currentLangCode = languageManager.getSelected().getCode();
        if (!currentLangCode.equals(LanguageManager.DEFAULT_LANGUAGE_CODE))
            selectedLanguages.add(currentLangCode);
        selectedLanguages.addAll(Config.getInstance().fallbacks);
        for (var language : languageManager.getLanguages()) {
            var code = language.getCode();
            if (!code.equals(LanguageManager.DEFAULT_LANGUAGE_CODE))
                languageEntries.put(code, new MovableLanguageEntry(this::refresh, code, language, selectedLanguages));
            else defaultLanguageEntry = new LockedLanguageEntry(this::refresh, code, language, selectedLanguages);
        }
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    void onInit(CallbackInfo ci) {
        searchBox = new EditBox(font, width / 2 - 100, 22, 200, 20, searchBox, Component.empty()) {
            @Override
            public void setFocused(boolean focused) {
                if (!isFocused() && focused) {
                    super.setFocused(true);
                    focusSearch();
                }
                else super.setFocused(focused);
            }
        };
        searchBox.setResponder(__ -> refresh());
        addWidget(searchBox);

        var listWidth = Math.min(width / 2 - 4, 200);
        var it = (LanguageSelectScreen) (Object) this;
        availableLanguageList = new LanguageListWidget(minecraft, it, listWidth, height, Component.translatable("pack.available.title"));
        selectedLanguageList = new LanguageListWidget(minecraft, it, listWidth, height, Component.translatable("pack.selected.title"));
        availableLanguageList.setLeftPos(width / 2 - 4 - listWidth);
        selectedLanguageList.setLeftPos(width / 2 + 4);
        addWidget(availableLanguageList);
        addWidget(selectedLanguageList);
        refresh();

        addRenderableWidget(options.forceUnicodeFont().createButton(options, width / 2 - 155, height - 28, 150));
        addRenderableWidget(new Button(width / 2 - 155 + 160, height - 28, 150, 20, CommonComponents.GUI_DONE, this::onDone));
        setInitialFocus(searchBox);

        super.init();
        ci.cancel();
    }

    @Unique
    private void onDone(Button button) {
        if (minecraft == null) return;
        minecraft.setScreen(lastScreen);

        var language = selectedLanguages.peekFirst();
        if (language == null) {
            LanguageReload.setLanguage(LanguageManager.DEFAULT_LANGUAGE_CODE, new LinkedList<>());
        } else {
            var fallbacks = new LinkedList<>(selectedLanguages);
            fallbacks.remove(0);
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
        refreshList(selectedLanguageList, Stream.concat(
                selectedLanguages.stream().map(languageEntries::get).filter(Objects::nonNull),
                Stream.of(defaultLanguageEntry)));
        refreshList(availableLanguageList, languageEntries.values().stream()
                .filter(entry -> {
                    if (selectedLanguageList.children().contains(entry)) return false;
                    var query = searchBox.getValue().toLowerCase(Locale.ROOT);
                    var langCode = entry.getCode().toLowerCase(Locale.ROOT);
                    var langName = entry.getLanguage().toString().toLowerCase(Locale.ROOT);
                    return langCode.contains(query) || langName.contains(query);
                }));
    }

    @Unique
    private void refreshList(LanguageListWidget list, Stream<? extends LanguageEntry> entries) {
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
        list.setScrollAmount(list.getScrollAmount());
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void onRender(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderDirtBackground(0);

        availableLanguageList.render(matrices, mouseX, mouseY, delta);
        selectedLanguageList.render(matrices, mouseX, mouseY, delta);
        searchBox.render(matrices, mouseX, mouseY, delta);

        drawCenteredString(matrices, font, title, width / 2, 8, 0xFFFFFF);
        drawCenteredString(matrices, font, WARNING_LABEL, width / 2, height - 46, 0x808080);

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