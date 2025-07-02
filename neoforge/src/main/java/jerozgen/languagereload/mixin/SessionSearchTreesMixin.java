package jerozgen.languagereload.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import jerozgen.languagereload.helper.SessionSearchTreesHelper;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = SessionSearchTrees.class, priority = 990)
abstract class SessionSearchTreesMixin {
    @WrapOperation(method = {"lambda$getTooltipLines$0"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;"))
    private static List<Component> addFallbackTranslationsToSearchTooltips(ItemStack items, Item.TooltipContext context, Player player, TooltipFlag type, Operation<List<Component>> operation) {
        return SessionSearchTreesHelper.addFallbackTranslationsToSearchTooltips(items, context, player, type, operation);
    }
}
