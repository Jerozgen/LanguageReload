package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IGameOptions;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin implements IGameOptions {
    @Shadow public String language;
    String previousLanguage = "";

    @Inject(method = "accept", at = @At("HEAD"))
    private void onAccept(GameOptions.Visitor visitor, CallbackInfo ci) {
        previousLanguage = visitor.visitString("previousLang", previousLanguage);
    }

    @Override
    public String getPreviousLanguage() {
        return previousLanguage;
    }

    @Override
    public void savePreviousLanguage() {
        previousLanguage = language;
    }
}
