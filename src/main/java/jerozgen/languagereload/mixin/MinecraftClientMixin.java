package jerozgen.languagereload.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import jerozgen.languagereload.access.ILanguage;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = MinecraftClient.class, priority = 990)
abstract class MinecraftClientMixin {
    @WrapOperation(method = {"method_1485" /* Creative Inventory */, "method_1591" /* Recipe Book */},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;"))
    private static List<Text> addFallbackTranslationsToSearchTooltips(ItemStack instance, PlayerEntity player, TooltipContext context, Operation<List<Text>> operation) {
        var original = operation.call(instance, player, context);

        if (Config.getInstance() == null) return original;
        if (!Config.getInstance().multilingualItemSearch) return original;

        var translationStorage = ((ILanguage) Language.getInstance()).languagereload_getTranslationStorage();
        if (translationStorage == null) return original;

        var result = new ArrayList<>(original);
        for (var fallbackCode : Config.getInstance().fallbacks) {
            ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(fallbackCode);
            operation.call(instance, player, context)
                    .stream()
                    .map(Text::getString)
                    .map(Text::literal)
                    .forEach(result::add);
        }

        ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(null);
        return result;
    }
}
