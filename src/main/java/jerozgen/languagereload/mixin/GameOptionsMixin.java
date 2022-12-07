package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.option.GameOptions;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameOptions.class)
abstract class GameOptionsMixin {
    @Inject(method = "load", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;accept(Lnet/minecraft/client/option/GameOptions$Visitor;)V"))
    void onLoad(CallbackInfo ci, NbtCompound nbtCompound, NbtCompound nbtCompound2) {
        LanguageReload.shouldSetSystemLanguage = !nbtCompound2.contains("lang");
    }
}
