package jerozgen.languagereload.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
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

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private boolean usedDebugKeyAsModifier;

    @Shadow protected abstract void debugWarningComponent(Component message);
    @Shadow protected abstract void debugFeedbackComponent(Component text);

    @Unique
    private void processLanguageReloadKeys(KeyEvent event) {
        if (event.hasShiftDown()) {
            var config = Config.getInstance();
            var languageManager = minecraft.getLanguageManager();

            var language = languageManager.getLanguage(config.previousLanguage);
            if (language == null && config.previousLanguage.equals(Language.DEFAULT)) {
                language = LanguageManagerAccessor.languagereload_getDefaultLanguage();
            }
            var noLanguage = config.previousLanguage.equals(LanguageReload.NO_LANGUAGE);
            if (language == null && !noLanguage) {
                this.debugWarningComponent(Component.translatable("debug.reload_languages.switch.failure"));
            } else {
                LanguageReload.setLanguage(config.previousLanguage, config.previousFallbacks);
                var languages = new ArrayList<Component>();
                if (noLanguage) {
                    languages.add(Component.nullToEmpty("∅"));
                }
                if (language != null) {
                    languages.add(language.toComponent());
                }
                languages.addAll(config.fallbacks.stream()
                        .map(languageManager::getLanguage)
                        .filter(Objects::nonNull)
                        .map(LanguageInfo::toComponent)
                        .toList());
                this.debugFeedbackComponent(Component.translatable("debug.reload_languages.switch.success", ComponentUtils.formatList(languages, Component.nullToEmpty(", "))));
            }
        } else {
            LanguageReload.reloadLanguages();
            this.debugFeedbackComponent(Component.translatable("debug.reload_languages.message"));
        }
    }

    @Inject(method = "handleDebugKeys", at = @At("RETURN"), cancellable = true)
    private void onHandleDebugKeys(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (LanguageReload.reloadLanguagesKey.matches(event)) {
            processLanguageReloadKeys(event);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/KeyboardHandler;debugCrashKeyTime:J", ordinal = 0, opcode = Opcodes.GETFIELD), cancellable = true)
    private void onKeyPress(long handle, int action, KeyEvent event, CallbackInfo ci) {
        if (minecraft.screen != null && minecraft.options.keyDebugModifier.isDown() && LanguageReload.reloadLanguagesKey.matches(event)) {
            this.usedDebugKeyAsModifier = true;
            if (action != InputConstants.PRESS) {
                processLanguageReloadKeys(event);
            }
            ci.cancel();
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void onCharTyped(long handle, CharacterEvent event, CallbackInfo ci) {
        if (minecraft.options.keyDebugModifier.isDown()) {
            var reloadLanguagesKeyCode = ((KeyMappingAccessor) LanguageReload.reloadLanguagesKey).languagereload_getKey().getValue();
            if (InputConstants.isKeyDown(minecraft.getWindow(), reloadLanguagesKeyCode)) {
                ci.cancel();
            }
        }
    }
}
