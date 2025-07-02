package jerozgen.languagereload.helper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import jerozgen.languagereload.access.IClientLanguage;
import jerozgen.languagereload.access.ILanguage;
import jerozgen.languagereload.config.Config;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class SessionSearchTreesHelper {
    public static List<Component> addFallbackTranslationsToSearchTooltips(ItemStack items, Item.TooltipContext context, Player player, TooltipFlag type, Operation<List<Component>> operation) {
        var original = operation.call(items, context, player, type);

        if (Config.getInstance() == null) return original;
        if (!Config.getInstance().multilingualItemSearch) return original;

        var language = Language.getInstance();
        if (language == null) return original;

        var clientLanguage = ((ILanguage) language).languagereload_getClientLanguage();
        if (clientLanguage == null) return original;

        var result = new ArrayList<>(original);
        for (var fallbackCode : Config.getInstance().fallbacks) {
            ((IClientLanguage) clientLanguage).languagereload_setTargetLanguage(fallbackCode);
            operation.call(items, context, player, type).stream()
                    .map(Component::getString)
                    .map(Component::literal)
                    .forEach(result::add);
        }

        ((IClientLanguage) clientLanguage).languagereload_setTargetLanguage(null);

        return result;
    }
}
