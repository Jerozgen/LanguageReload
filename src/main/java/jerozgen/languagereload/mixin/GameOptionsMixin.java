package jerozgen.languagereload.mixin;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.option.GameOptions;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
abstract class GameOptionsMixin {
    @Inject(method = "update", at = @At("RETURN"))
    void checkMissingLang(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        LanguageReload.shouldSetSystemLanguage = !cir.getReturnValue().contains("lang");
    }
}
