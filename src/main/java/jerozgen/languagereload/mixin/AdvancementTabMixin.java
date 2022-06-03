package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IAdvancementsTab;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Objects;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin extends DrawableHelper implements IAdvancementsTab {
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private Map<Advancement, AdvancementWidget> widgets;

    @Override
    public void recreateWidgets() {
        widgets.replaceAll((advancement, widget) -> {
            var newWidget = new AdvancementWidget(
                    ((AdvancementWidgetAccessor) widget).getTab(),
                    client,
                    advancement,
                    Objects.requireNonNull(advancement.getDisplay())
            );
            newWidget.setProgress(((AdvancementWidgetAccessor) widget).getProgress());
            ((AdvancementWidgetAccessor) newWidget).setParent(((AdvancementWidgetAccessor) widget).getParent());
            ((AdvancementWidgetAccessor) newWidget).setChildren(((AdvancementWidgetAccessor) widget).getChildren());
            return newWidget;
        });
    }
}
