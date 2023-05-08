package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.BufferedReader;

@Mixin(Options.class)
abstract class GameOptionsMixin {
    @Inject(method = "load", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/Options;processOptions(Lnet/minecraft/client/Options$FieldAccess;)V"))
    void onLoad(CallbackInfo ci, CompoundTag compoundtag, BufferedReader bufferedreader, CompoundTag compoundtag1) {
        LanguageReload.shouldSetSystemLanguage = !compoundtag1.contains("lang");
    }
}