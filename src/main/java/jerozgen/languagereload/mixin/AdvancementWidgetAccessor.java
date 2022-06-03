package jerozgen.languagereload.mixin;

import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {
    @Accessor
    AdvancementTab getTab();

    @Accessor
    AdvancementProgress getProgress();

    @Accessor
    AdvancementWidget getParent();

    @Accessor
    void setParent(AdvancementWidget parent);

    @Accessor
    List<AdvancementWidget> getChildren();

    @Mutable
    @Accessor
    void setChildren(List<AdvancementWidget> children);
}
