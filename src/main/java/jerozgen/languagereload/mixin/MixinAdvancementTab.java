package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IAdvancementWidget;
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
public abstract class MixinAdvancementTab extends DrawableHelper implements IAdvancementsTab {
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private Map<Advancement, AdvancementWidget> widgets;

    @Override
    public void recreateWidgets() {
        widgets.replaceAll((advancement, widget) -> {
            AdvancementWidget newWidget = new AdvancementWidget(
                    ((IAdvancementWidget) widget).getTab(),
                    client,
                    advancement,
                    Objects.requireNonNull(advancement.getDisplay())
            );
            newWidget.setProgress(((IAdvancementWidget) widget).getProgress());
            ((IAdvancementWidget) newWidget).setParent(((IAdvancementWidget) widget).getParent());
            ((IAdvancementWidget) newWidget).setChildren(((IAdvancementWidget) widget).getChildren());
            return newWidget;
        });
    }
}
