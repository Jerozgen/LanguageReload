package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(GameConfig args, CallbackInfo ci) {
        Config.load();
    }

    @Redirect(method = {"lambda$createSearchTrees$5" /* Creative Inventory */, "lambda$createSearchTrees$12" /* Recipe Book */},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;"))
    private static String addFallbackTranslationsToSearchTooltips(Component text) {
        if (Config.getInstance() == null) return text.getString();
        if (!Config.getInstance().multilingualItemSearch) return text.getString();
        if (!(Language.getInstance() instanceof ClientLanguage translationStorage)) return text.getString();

        var stringBuilder = new StringBuilder(text.getString());
        for (String fallbackCode : Config.getInstance().fallbacks) {
            ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(fallbackCode);
            stringBuilder.append('\n').append(text.getString());
        }
        ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(Language.DEFAULT);
        stringBuilder.append('\n').append(text.getString());

        ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(null);
        return stringBuilder.toString();
    }
}