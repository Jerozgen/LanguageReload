package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import jerozgen.languagereload.gui.LanguageListWidget;
import jerozgen.languagereload.gui.LockedLanguageEntry;
import jerozgen.languagereload.gui.MovableLanguageEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen {
    @Shadow @Final private static Text LANGUAGE_WARNING_TEXT;

    @Shadow @Final LanguageManager languageManager;
    private LanguageListWidget availableLanguageList;
    private LanguageListWidget selectedLanguageList;
    private TextFieldWidget searchBox;
    private final LinkedList<LanguageDefinition> selectedLanguages = new LinkedList<>();
    private final Map<LanguageDefinition, MovableLanguageEntry> languageEntries = new LinkedHashMap<>();
    private LockedLanguageEntry defaultLanguageEntry;

    LanguageOptionsScreenMixin(Screen parent, GameOptions options, Text title) {
        super(parent, options, title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Screen parent, GameOptions options, LanguageManager languageManager, CallbackInfo ci) {
        var currentLang = languageManager.getLanguage();
        if (!currentLang.getCode().equals(LanguageManager.DEFAULT_LANGUAGE_CODE)) selectedLanguages.add(currentLang);

        var fallbacks = Config.getInstance().fallbacks.stream()
                .map(languageManager::getLanguage)
                .filter(Objects::nonNull)
                .toList();
        selectedLanguages.addAll(fallbacks);

        for (var language : languageManager.getAllLanguages()) {
            if (!language.getCode().equals(LanguageManager.DEFAULT_LANGUAGE_CODE)) {
                languageEntries.put(language, new MovableLanguageEntry(this::refresh, language, selectedLanguages));
            } else {
                defaultLanguageEntry = new LockedLanguageEntry(this::refresh, language, selectedLanguages);
            }
        }
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    void onInit(CallbackInfo ci) {
        searchBox = new TextFieldWidget(textRenderer, width / 2 - 100, 22, 200, 20, searchBox, Text.empty());
        searchBox.setChangedListener(__ -> refresh());
        addSelectableChild(searchBox);
        setInitialFocus(searchBox);

        var listWidth = Math.min(width / 2 - 4, 200);
        availableLanguageList = new LanguageListWidget(client, listWidth, height, Text.translatable("pack.available.title"));
        selectedLanguageList = new LanguageListWidget(client, listWidth, height, Text.translatable("pack.selected.title"));
        availableLanguageList.setLeftPos(width / 2 - 4 - listWidth);
        selectedLanguageList.setLeftPos(width / 2 + 4);
        addSelectableChild(availableLanguageList);
        addSelectableChild(selectedLanguageList);
        refresh();

        addDrawableChild(gameOptions.getForceUnicodeFont().createButton(gameOptions, width / 2 - 155, height - 28, 150));
        addDrawableChild(new ButtonWidget(width / 2 - 155 + 160, height - 28, 150, 20, ScreenTexts.DONE, this::onDone));

        super.init();
        ci.cancel();
    }

    private void onDone(ButtonWidget button) {
        if (client == null) return;
        client.setScreen(parent);

        var language = selectedLanguages.peekFirst();
        if (language == null) language = languageManager.getLanguage(LanguageManager.DEFAULT_LANGUAGE_CODE);
        var fallbacks = selectedLanguages.stream()
                .skip(1)
                .map(LanguageDefinition::getCode)
                .collect(Collectors.toCollection(LinkedList::new));

        LanguageReload.setLanguage(language, fallbacks);
    }

    private void refresh() {
        selectedLanguageList.children().clear();
        selectedLanguages.forEach(language -> {
            var entry = languageEntries.get(language);
            if (entry != null) selectedLanguageList.children().add(entry);
        });
        selectedLanguageList.children().add(defaultLanguageEntry);

        availableLanguageList.children().clear();
        languageEntries.forEach((lang, entry) -> {
            if (selectedLanguageList.children().contains(entry)) return;
            var langName = entry.getLanguage().toString().toLowerCase(Locale.ROOT);
            var langCode = entry.getLanguage().getCode().toLowerCase(Locale.ROOT);
            var query = searchBox.getText().toLowerCase(Locale.ROOT);
            if (langName.contains(query) || langCode.contains(query)) {
                availableLanguageList.children().add(entry);
            }
        });
        availableLanguageList.setScrollAmount(availableLanguageList.getScrollAmount());
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
