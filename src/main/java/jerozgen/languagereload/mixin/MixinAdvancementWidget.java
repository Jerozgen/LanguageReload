package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IAdvancementWidget;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(AdvancementWidget.class)
public abstract class MixinAdvancementWidget extends DrawableHelper implements IAdvancementWidget {
    @Shadow @Final private AdvancementTab tab;
    @Shadow private AdvancementWidget parent;
    @Shadow private AdvancementProgress progress;
    @Shadow @Final @Mutable private List<AdvancementWidget> children;

    @Override
    public AdvancementTab getTab() {
        return tab;
    }

    @Override
    public AdvancementProgress getProgress() {
        return progress;
    }

    @Override
    public AdvancementWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(AdvancementWidget parent) {
        this.parent = parent;
    }

    @Override
    public List<AdvancementWidget> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<AdvancementWidget> children) {
        this.children = children;
    }
}
