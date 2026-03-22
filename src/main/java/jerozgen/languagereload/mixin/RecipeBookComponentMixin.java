package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {
    @Inject(method = "pirateSpeechForThePeople", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/language/LanguageManager;setSelected(Ljava/lang/String;)V"))
    void onPirateSpeechForThePeople$cancelReload(String searchTarget, CallbackInfo ci) {
        LanguageReload.setLanguage("en_pt");
        ci.cancel();
    }
}
