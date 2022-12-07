package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Redirect(method = "triggerPirateSpeakEasterEgg", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resource/language/LanguageManager;setLanguage(Lnet/minecraft/client/resource/language/LanguageDefinition;)V"))
    void onLanguageSwitching$redirect(LanguageManager instance, LanguageDefinition language) {
        LanguageReload.setLanguage(language, new LinkedList<>());
    }

    @Inject(method = "triggerPirateSpeakEasterEgg", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/resource/language/LanguageManager;setLanguage(Lnet/minecraft/client/resource/language/LanguageDefinition;)V"))
    void onLanguageSwitching$cancel(String search, CallbackInfo ci) {
        ci.cancel();
    }
}
