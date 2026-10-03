package jerozgen.languagereload.mixin;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetAccessor {
    @Invoker("<init>")
    static AdvancementWidget languagereload_create(Minecraft minecraft, AdvancementNode advancementNode, DisplayInfo display) {
        throw new AssertionError();
    }

    @Accessor("advancementNode")
    AdvancementNode languagereload_getAdvancementNode();

    @Accessor("display")
    DisplayInfo languagereload_getDisplay();

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
