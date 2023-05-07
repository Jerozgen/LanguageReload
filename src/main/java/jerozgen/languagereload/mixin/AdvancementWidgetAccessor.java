package jerozgen.languagereload.mixin;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {
    @Accessor("tab")
    AdvancementTab languagereload_getTab();

    @Accessor("progress")
    AdvancementProgress languagereload_getProgress();

    @Accessor("parent")
    AdvancementWidget languagereload_getParent();

    @Accessor("parent")
    void languagereload_setParent(AdvancementWidget parent);

    @Accessor("children")
    List<AdvancementWidget> languagereload_getChildren();

    @Mutable
    @Accessor("children")
    void languagereload_setChildren(List<AdvancementWidget> children);
}