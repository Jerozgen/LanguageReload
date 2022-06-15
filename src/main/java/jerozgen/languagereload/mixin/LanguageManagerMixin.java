package jerozgen.languagereload.mixin;

import jerozgen.languagereload.config.Config;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Objects;

@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin {
    @Shadow public abstract LanguageDefinition getLanguage(String code);

    @Inject(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            remap = false), locals = LocalCapture.CAPTURE_FAILSOFT)
    void onReload$addFallbacks(ResourceManager manager, CallbackInfo ci, LanguageDefinition languageDefinition, List<LanguageDefinition> list) {
        Config.getInstance().fallbacks.stream()
                .map(this::getLanguage)
                .filter(Objects::nonNull)
                .forEach(list::add);
    }
}
