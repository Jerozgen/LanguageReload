package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(RunArgs args, CallbackInfo ci) {
        Config.load();
    }

    @Redirect(method = {"method_1581" /* Creative Inventory */, "method_43765" /* Recipe Book */},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;"))
    private static String addFallbackTranslationsToSearchTooltips(Text text) {
        if (Config.getInstance() == null) return text.getString();
        if (!Config.getInstance().multilingualItemSearch) return text.getString();
        if (!(Language.getInstance() instanceof TranslationStorage translationStorage)) return text.getString();

        var stringBuilder = new StringBuilder(text.getString());
        for (String fallbackCode : Config.getInstance().fallbacks) {
            ((ITranslationStorage) translationStorage).setTargetLanguage(fallbackCode);
            stringBuilder.append('\n').append(text.getString());
        }
        ((ITranslationStorage) translationStorage).setTargetLanguage(Language.DEFAULT_LANGUAGE);
        stringBuilder.append('\n').append(text.getString());

        ((ITranslationStorage) translationStorage).setTargetLanguage(null);
        return stringBuilder.toString();
    }
}
