package jerozgen.languagereload.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
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

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void debugLog(String key, Object... args);

    private void reloadLanguages() {
        client.getLanguageManager().reload(client.getResourceManager());
        this.debugLog("debug.reload_languages.message");
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
            at = @At("TAIL"),
            cancellable = true
    )
    private void onProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == GLFW.GLFW_KEY_J) {
            reloadLanguages();
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
            if (action != 0) reloadLanguages();
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
