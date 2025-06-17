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

    @Shadow protected abstract void sendMessage(Text message);
    @Shadow protected abstract void debugError(Text message);
    @Shadow protected abstract void debugLog(Text text);
    @Shadow protected abstract void debugLog(String key);

    @Unique
    private void processLanguageReloadKeys() {
        if (Screen.hasShiftDown()) {
            var config = Config.getInstance();
            var languageManager = client.getLanguageManager();

            var language = languageManager.getLanguage(config.previousLanguage);
            var noLanguage = config.previousLanguage.equals(LanguageReload.NO_LANGUAGE);
            if (language == null && !noLanguage) {
                this.debugError(Text.translatable("debug.reload_languages.switch.failure"));
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
                this.debugLog(Text.translatable("debug.reload_languages.switch.success", Texts.join(languages, Text.of(", "))));
            }
        } else {
            LanguageReload.reloadLanguages();
            this.debugLog("debug.reload_languages.message");
        }
    }

    @Inject(method = "processF3", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Keyboard;sendMessage(Lnet/minecraft/text/Text;)V",
            ordinal = 6, shift = At.Shift.AFTER))
    private void onProcessF3$addHelp(int key, CallbackInfoReturnable<Boolean> cir) {
        this.sendMessage(Text.translatable("debug.reload_languages.help"));
    }

    @Inject(method = "processF3", at = @At("RETURN"), cancellable = true)
    private void onProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GLFW.GLFW_KEY_J) {
            processLanguageReloadKeys();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0), cancellable = true)
    private void onOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (client.currentScreen != null && InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_F3) && key == GLFW.GLFW_KEY_J) {
            if (action != 0) {
                processLanguageReloadKeys();
            }
            ci.cancel();
        }
    }

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void onOnChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_F3) && InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_J)) {
            ci.cancel();
        }
    }
}
