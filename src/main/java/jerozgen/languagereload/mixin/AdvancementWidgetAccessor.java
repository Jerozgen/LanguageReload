package jerozgen.languagereload.mixin;

import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {
    @Accessor("tab")
    AdvancementTab languagereload_getTab();

    @Accessor("advancement")
    PlacedAdvancement languagereload_getAdvancement();

    @Accessor("display")
    AdvancementDisplay languagereload_getDisplay();

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
