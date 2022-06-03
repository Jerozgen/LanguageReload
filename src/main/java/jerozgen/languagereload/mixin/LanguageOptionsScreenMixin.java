package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.IGameOptions;
import jerozgen.languagereload.access.ILanguageOptionsScreen;
import jerozgen.languagereload.access.ILanguageSelectionListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen implements ILanguageOptionsScreen {
    @Shadow private LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionList;
    TextFieldWidget searchBox;

    public LanguageOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @SuppressWarnings("target")
    @Inject(method = "method_19820(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resource/language/LanguageManager;setLanguage(Lnet/minecraft/client/resource/language/LanguageDefinition;)V"))
    private void onLanguageSwitching(CallbackInfo ci) {
        ((IGameOptions) gameOptions).savePreviousLanguage();
    }

    @SuppressWarnings("target")
    @Redirect(method = "method_19820(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;reloadResources()Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> onLanguageSwitching$reloadResourcesRedirect(MinecraftClient client) {
        LanguageReload.reloadLanguages(client);
        return null;
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        searchBox = new TextFieldWidget(textRenderer, width / 2 - 100, 22, 200, 20, searchBox, Text.empty());
        searchBox.setChangedListener(text -> ((ILanguageSelectionListWidget) languageSelectionList).filter(text));
        addSelectableChild(searchBox);
        setInitialFocus(searchBox);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        searchBox.render(matrices, mouseX, mouseY, delta);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/option/LanguageOptionsScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V",
            ordinal = 0), index = 4)
    public int onRender$moveTitleTextUp(int y) {
        return 8;
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
        return this.searchBox.charTyped(chr, modifiers);
    }

    @Override
    public String getSearchText() {
        return searchBox.getText();
    }
}
