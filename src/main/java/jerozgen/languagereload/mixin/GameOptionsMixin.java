package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(GameOptions.class)
abstract class GameOptionsMixin {
    @Shadow @Final private File optionsFile;
    @Shadow public String language;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(MinecraftClient client, File optionsFile, CallbackInfo ci) {
        if (!LanguageReload.shouldSetSystemLanguage) {
            checkConfigLanguage(language);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    void onLoad(CallbackInfo ci) {
        if (!optionsFile.exists()) {
            LanguageReload.shouldSetSystemLanguage = true;
        }
    }

    @Inject(method = "update", at = @At("RETURN"))
    void onUpdate(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        var lang = cir.getReturnValue().getString("lang");
        if (lang.isEmpty()) {
            LanguageReload.shouldSetSystemLanguage = true;
        } else checkConfigLanguage(lang);
    }

    @Unique
    private static void checkConfigLanguage(String language) {
        var config = Config.getInstance();
        if (!config.language.equals(language)) {
            LanguageReload.LOGGER.info(
                    "Game language ({}) and config language ({}) are different. Updating config",
                    language,
                    config.language
            );
            config.previousLanguage = config.language;
            config.previousFallbacks = config.fallbacks;
            config.language = language;
            config.fallbacks.clear();
            if (!language.equals(Language.DEFAULT_LANGUAGE))
                config.fallbacks.add(Language.DEFAULT_LANGUAGE);
            Config.save();
        }
    }
}
