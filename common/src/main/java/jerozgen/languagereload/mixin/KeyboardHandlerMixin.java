package jerozgen.languagereload.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
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

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void showDebugChat(Component message);
    @Shadow protected abstract void debugWarningComponent(Component message);
    @Shadow protected abstract void debugFeedbackComponent(Component text);
    @Shadow protected abstract void debugFeedbackTranslated(String key);

    @Unique
    private void languagereload_processLanguageReloadKeys() {
        if (Screen.hasShiftDown()) {
            var config = Config.getInstance();
            var languageManager = minecraft.getLanguageManager();

            var language = languageManager.getLanguage(config.previousLanguage);
            var noLanguage = config.previousLanguage.equals(LanguageReload.NO_LANGUAGE);
            if (language == null && !noLanguage) {
                this.debugWarningComponent(Component.translatable("debug.reload_languages.switch.failure"));
            } else {
                LanguageReload.setLanguage(config.previousLanguage, config.previousFallbacks);
                var languages = new ArrayList<Component>() {{
                    if (noLanguage)
                        add(Component.literal("∅"));
                    if (language != null)
                        add(language.toComponent());
                    addAll(config.fallbacks.stream()
                            .map(languageManager::getLanguage)
                            .filter(Objects::nonNull)
                            .map(LanguageInfo::toComponent)
                            .toList());
                }};
                this.debugFeedbackComponent(Component.translatable("debug.reload_languages.switch.success", ComponentUtils.formatList(languages, Component.literal(", "))));
            }
        } else {
            LanguageReload.reloadLanguages();
            this.debugFeedbackTranslated("debug.reload_languages.message");
        }
    }

    @Inject(method = "handleDebugKeys", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/KeyboardHandler;showDebugChat(Lnet/minecraft/network/chat/Component;)V",
            ordinal = 6, shift = At.Shift.AFTER))
    private void onProcessF3$addHelp(int key, CallbackInfoReturnable<Boolean> cir) {
        this.showDebugChat(Component.translatable("debug.reload_languages.help"));
    }

    @Inject(method = "handleDebugKeys", at = @At("RETURN"), cancellable = true)
    private void onProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GLFW.GLFW_KEY_J) {
            languagereload_processLanguageReloadKeys();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyboardHandler;debugCrashKeyTime:J", ordinal = 0), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (minecraft.screen != null && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_F3) && key == GLFW.GLFW_KEY_J) {
            if (action != 0) {
                languagereload_processLanguageReloadKeys();
            }
            ci.cancel();
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void onCharTyped(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_F3) && InputConstants.isKeyDown(window, GLFW.GLFW_KEY_J)) {
            ci.cancel();
        }
    }
}
