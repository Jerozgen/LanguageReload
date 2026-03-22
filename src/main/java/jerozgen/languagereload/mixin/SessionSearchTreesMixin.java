package jerozgen.languagereload.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import jerozgen.languagereload.access.IClientLanguage;
import jerozgen.languagereload.access.ILanguage;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SessionSearchTrees.class, priority = 990)
public abstract class SessionSearchTreesMixin {
    @WrapOperation(method = {"method_60365"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;"))
    private static List<Component> addFallbackTranslationsToSearchTooltips(ItemStack instance, Item.TooltipContext context, @Nullable Player player, TooltipFlag flag, Operation<List<Component>> operation) {
        var original = operation.call(instance, context, player, flag);

        if (Config.getInstance() == null) return original;
        if (!Config.getInstance().multilingualItemSearch) return original;

        var language = Language.getInstance();
        if (language == null) return original;

        var clientLanguage = ((ILanguage) language).languagereload_getClientLanguage();
        if (clientLanguage == null) return original;

        var result = new ArrayList<>(original);
        for (var fallbackCode : Config.getInstance().fallbacks) {
            ((IClientLanguage) clientLanguage).languagereload_setTargetLanguage(fallbackCode);
            operation.call(instance, context, player, flag)
                    .stream()
                    .map(Component::getString)
                    .map(Component::literal)
                    .forEach(result::add);
        }

        ((IClientLanguage) clientLanguage).languagereload_setTargetLanguage(null);
        return result;
    }
}
