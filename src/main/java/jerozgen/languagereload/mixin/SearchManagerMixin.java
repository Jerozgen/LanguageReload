package jerozgen.languagereload.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import jerozgen.languagereload.access.ILanguage;
import jerozgen.languagereload.access.ITranslationStorage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.search.SearchManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SearchManager.class, priority = 990)
abstract class SearchManagerMixin {
    @WrapOperation(method = {"method_60365"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;"))
    private static List<Text> addFallbackTranslationsToSearchTooltips(ItemStack instance, Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, Operation<List<Text>> operation) {
        var original = operation.call(instance, context, player, type);

        if (Config.getInstance() == null) return original;
        if (!Config.getInstance().multilingualItemSearch) return original;

        var language = Language.getInstance();
        if (language == null) return original;

        var translationStorage = ((ILanguage) language).languagereload_getTranslationStorage();
        if (translationStorage == null) return original;

        var result = new ArrayList<>(original);
        for (var fallbackCode : Config.getInstance().fallbacks) {
            ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(fallbackCode);
            operation.call(instance, context, player, type)
                    .stream()
                    .map(Text::getString)
                    .map(Text::literal)
                    .forEach(result::add);
        }

        ((ITranslationStorage) translationStorage).languagereload_setTargetLanguage(null);
        return result;
    }
}
