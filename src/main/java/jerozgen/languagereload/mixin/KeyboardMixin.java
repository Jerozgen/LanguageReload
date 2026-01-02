package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Language;
import org.objectweb.asm.Opcodes;
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

    @Shadow protected abstract void debugError(Text message);
    @Shadow protected abstract void debugLog(Text text);

    @Unique
    private void processLanguageReloadKeys(KeyInput input) {
        if (input.hasShift()) {
            var config = Config.getInstance();
            var languageManager = client.getLanguageManager();

            var language = languageManager.getLanguage(config.previousLanguage);
            if (language == null && config.previousLanguage.equals(Language.DEFAULT_LANGUAGE)) {
                language = LanguageManagerAccessor.languagereload_getEnglishUs();
            }
            var noLanguage = config.previousLanguage.equals(LanguageReload.NO_LANGUAGE);
            if (language == null && !noLanguage) {
                this.debugError(Text.translatable("debug.reload_languages.switch.failure"));
            } else {
                LanguageReload.setLanguage(config.previousLanguage, config.previousFallbacks);
                var languages = new ArrayList<Text>();
                if (noLanguage) {
                    languages.add(Text.of("∅"));
                }
                if (language != null) {
                    languages.add(language.getDisplayText());
                }
                languages.addAll(config.fallbacks.stream()
                        .map(languageManager::getLanguage)
                        .filter(Objects::nonNull)
                        .map(LanguageDefinition::getDisplayText)
                        .toList());
                this.debugLog(Text.translatable("debug.reload_languages.switch.success", Texts.join(languages, Text.of(", "))));
            }
        } else {
            LanguageReload.reloadLanguages();
            this.debugLog(Text.translatable("debug.reload_languages.message"));
        }
    }

    @Inject(method = "processF3", at = @At("RETURN"), cancellable = true)
    private void onProcessF3(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (LanguageReload.reloadLanguagesKey.matchesKey(keyInput)) {
            processLanguageReloadKeys(keyInput);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0, opcode = Opcodes.GETFIELD), cancellable = true)
    private void onOnKey(long window, int action, KeyInput input, CallbackInfo ci) {
        if (client.currentScreen != null && client.options.debugModifierKey.isPressed() && LanguageReload.reloadLanguagesKey.matchesKey(input)) {
            this.switchF3State = true;
            if (action != InputUtil.GLFW_PRESS) {
                processLanguageReloadKeys(input);
            }
            ci.cancel();
        }
    }

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void onOnChar(long window, CharInput input, CallbackInfo ci) {
        if (client.options.debugModifierKey.isPressed()) {
            var reloadLanguagesKeyCode = ((KeyBindingAccessor) LanguageReload.reloadLanguagesKey).languagereload_getBoundKey().getCode();
            if (InputUtil.isKeyPressed(client.getWindow(), reloadLanguagesKeyCode)) {
                ci.cancel();
            }
        }
    }
}
