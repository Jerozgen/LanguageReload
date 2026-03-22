package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow @Final private File optionsFile;
    @Shadow public String languageCode;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onConstructed(Minecraft minecraft, File workingDirectory, CallbackInfo ci) {
        if (!LanguageReload.shouldSetSystemLanguage) {
            checkConfigLanguage(languageCode);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    void onLoad(CallbackInfo ci) {
        if (!optionsFile.exists()) {
            LanguageReload.shouldSetSystemLanguage = true;
        }
    }

    @Inject(method = "dataFix", at = @At("RETURN"))
    void onDataFix(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        var lang = cir.getReturnValue().getStringOr("lang", "");
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
            if (!language.equals(Language.DEFAULT))
                config.fallbacks.add(Language.DEFAULT);
            Config.save();
        }
    }
}
