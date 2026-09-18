package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IAdvancementsTab;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin implements IAdvancementsTab {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private Map<AdvancementHolder, AdvancementWidget> widgets;

    @Override
    public void languagereload_recreateWidgets() {
        widgets.replaceAll((_, widget) -> {
            var accessor = (AdvancementWidgetAccessor) widget;

            var newWidget = AdvancementWidgetInvoker.languagereload_create(
                    minecraft,
                    accessor.languagereload_getAdvancementNode(),
                    accessor.languagereload_getDisplay()
            );

            newWidget.setProgress(
                    accessor.languagereload_getProgress()
            );

            ((AdvancementWidgetAccessor) newWidget)
                    .languagereload_setParent(
                            accessor.languagereload_getParent()
                    );

            ((AdvancementWidgetAccessor) newWidget)
                    .languagereload_setChildren(
                            accessor.languagereload_getChildren()
                    );

            return newWidget;
        });
    }
}
