package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Objects;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow private boolean switchF3State;

    @Shadow
    protected abstract void debugLog(String key, Object... args);

    @Shadow
    protected abstract void debugError(String key, Object... args);

    @Unique
    private void processLanguageReloadKeys() {
        if (Screen.hasShiftDown()) {
            var config = Config.getInstance();
            var languageManager = client.getLanguageManager();

            var language = languageManager.getLanguage(config.previousLanguage);
            var noLanguage = config.previousLanguage.equals(LanguageReload.NO_LANGUAGE);
            if (language == null && !noLanguage) {
                debugError("debug.reload_languages.switch.failure");
            } else {
                LanguageReload.setLanguage(config.previousLanguage, config.previousFallbacks);
                var languages = new ArrayList<Text>() {{
                    if (noLanguage)
                        add(Text.of("∅"));
                    if (language != null)
                        add(language.getDisplayText());
                    addAll(config.fallbacks.stream()
                            .map(languageManager::getLanguage)
                            .filter(Objects::nonNull)
                            .map(LanguageDefinition::getDisplayText)
                            .toList());
                }};
                debugLog("debug.reload_languages.switch.success", Texts.join(languages, Text.of(", ")));
            }
        } else {
            LanguageReload.reloadLanguages();
            debugLog("debug.reload_languages.message");
        }
    }

    @Inject(method = "processF3", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V",
            ordinal = 6, shift = At.Shift.AFTER))
    private void onProcessF3$addHelp(int key, CallbackInfoReturnable<Boolean> cir) {
        client.inGameHud.getChatHud().addMessage(Text.translatable("debug.reload_languages.help"));
    }

    @Inject(method = "processF3", at = @At("RETURN"), cancellable = true)
    private void onProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GLFW.GLFW_KEY_J) {
            processLanguageReloadKeys();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onKey", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"),
            cancellable = true)
    private void onOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_F3) && key == GLFW.GLFW_KEY_J) {
            if (action != 0) {
                if (Objects.requireNonNull(client.currentScreen).passEvents) {
                    switchF3State = true;
                }
                processLanguageReloadKeys();
            }
            ci.cancel();
        }
    }

    @Inject(method = "onChar", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
            ordinal = 0), cancellable = true)
    private void onOnChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_F3) && InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_J)) {
            ci.cancel();
        }
    }
}
