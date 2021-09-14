package jerozgen.languagereload.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void debugLog(String key, Object... args);

    @Inject(
            method = "processF3(I)Z",
            at = @At("TAIL"),
            cancellable = true
    )
    private void onProcessF3$addKey(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == 'J') {
            client.getLanguageManager().reload(client.getResourceManager());
            this.debugLog("debug.reload_languages.message");
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "processF3(I)Z",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;getChatHud()Lnet/minecraft/client/gui/hud/ChatHud;",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void onProcessF3$addHelp(int key, CallbackInfoReturnable<Boolean> cir) {
        this.client.inGameHud.getChatHud().addMessage(new TranslatableText("debug.reload_languages.help"));
    }
}
