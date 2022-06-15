package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin {
    @Shadow protected MinecraftClient client;

    @Inject(method = "triggerPirateSpeakEasterEgg", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resource/language/LanguageManager;setLanguage(Lnet/minecraft/client/resource/language/LanguageDefinition;)V"))
    private void onLanguageSwitching(CallbackInfo ci) {
        var config = Config.getInstance();
        config.previousLanguage = client.options.language;
        config.previousFallbacks = config.fallbacks;
        config.language = "en_pt";
        config.fallbacks = new LinkedList<>();
    }

    @Redirect(method = "triggerPirateSpeakEasterEgg", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;reloadResources()Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> onLanguageSwitching$reloadResourcesRedirect(MinecraftClient client) {
        LanguageReload.reloadLanguages(client);
        return null;
    }
}
