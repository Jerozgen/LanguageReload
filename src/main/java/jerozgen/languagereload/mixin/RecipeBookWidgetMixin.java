package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Inject(method = "triggerPirateSpeakEasterEgg", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resource/language/LanguageManager;setLanguage(Ljava/lang/String;)V"))
    void onLanguageSwitching$cancel(String search, CallbackInfo ci) {
        LanguageReload.setLanguage("en_pt", new LinkedList<>());
        ci.cancel();
    }
}
