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

    // targeting lambdas directly does not work for production environment since no mappings are generated for them during the build process
    // https://github.com/SpongePowered/Mixin/issues/355
    // That's why for dev environments m_210806_ must be replaced by lambda$createSearchTrees$6 and m_231454_ by lambda$createSearchTrees$14
    // It's probably possible to fix this issue by using extraMappings but there seems to be no proper documentation or examples on this.
    @Redirect(method = {"m_210806_" /* Creative Inventory */, "m_231454_" /* Recipe Book */},
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