package jerozgen.languagereload.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(LanguageOptionsScreen.class)
public abstract class MixinLanguageOptionsScreen extends GameOptionsScreen {
    public MixinLanguageOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
            method = "method_19820(Lnet/minecraft/client/gui/widget/ButtonWidget;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;reloadResources()Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private CompletableFuture<Void> reloadResourcesRedirect(MinecraftClient client) {
        client.getLanguageManager().reload(client.getResourceManager());
        return null;
    }
}
