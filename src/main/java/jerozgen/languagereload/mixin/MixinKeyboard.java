package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.IGameOptions;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {
    @Shadow @Final private MinecraftClient client;
    @Shadow private boolean switchF3State;

    @Shadow
    protected abstract void debugLog(String key, Object... args);

    @Shadow
    protected abstract void debugError(String key, Object... args);

    private void processLanguageReloadKeys() {
        if (Screen.hasShiftDown()) {
            LanguageManager languageManager = client.getLanguageManager();
            String previousCode = ((IGameOptions) client.options).getPreviousLanguage();
            LanguageDefinition previousLanguage = languageManager.getLanguage(previousCode);
            if (previousLanguage != null && !previousCode.equals("") && !previousCode.equals(client.options.language)) {
                languageManager.setLanguage(previousLanguage);
                ((IGameOptions) client.options).savePreviousLanguage();
                client.options.language = previousCode;
                LanguageReload.reloadLanguages(client);
                client.options.write();
                this.debugLog("debug.reload_languages.switch.success", previousLanguage.toString());
            } else {
                this.debugError("debug.reload_languages.switch.failure");
            }
        } else {
            LanguageReload.reloadLanguages(client);
            this.debugLog("debug.reload_languages.message");
        }
    }

    @Inject(
            method = "processF3(I)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V",
                    ordinal = 7,
                    shift = At.Shift.AFTER
            )
    )
    private void onProcessF3$addHelp(int key, CallbackInfoReturnable<Boolean> cir) {
        client.inGameHud.getChatHud().addMessage(new TranslatableText("debug.reload_languages.help"));
    }

    @Inject(
            method = "processF3(I)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GLFW.GLFW_KEY_J) {
            processLanguageReloadKeys();
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "onKey(JIIII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"
            ),
            cancellable = true
    )
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

    @Inject(
            method = "onChar(JII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void onOnChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_F3) && InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_J)) {
            ci.cancel();
        }
    }
}
