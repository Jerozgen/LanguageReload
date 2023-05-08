package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

@Mixin(RecipeBookComponent.class)
public class RecipeBookWidgetMixin {
    @Inject(method = "pirateSpeechForThePeople", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/language/LanguageManager;setSelected(Lnet/minecraft/client/resources/language/LanguageInfo;)V"))
    void onLanguageSwitching$cancel(String search, CallbackInfo ci) {
        LanguageReload.setLanguage("en_pt", new LinkedList<>());
        ci.cancel();
    }
}