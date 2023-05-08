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
import java.util.function.Consumer;

@Mixin(Options.class)
abstract class GameOptionsMixin {
    @Inject(method = "load(Z)V", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
        target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"), remap = false)
    void onLoad(boolean limited, CallbackInfo ci, CompoundTag compoundtag, BufferedReader bufferedreader, CompoundTag compoundtag1, Consumer processor) {
        LanguageReload.shouldSetSystemLanguage = !compoundtag1.contains("lang");
    }
}