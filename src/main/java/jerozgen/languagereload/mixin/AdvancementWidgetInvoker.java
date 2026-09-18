package jerozgen.languagereload.mixin;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AdvancementWidget.class)
public interface AdvancementWidgetInvoker {

    @Invoker("<init>")
    static AdvancementWidget languagereload_create(
            Minecraft minecraft,
            AdvancementNode advancementNode,
            DisplayInfo display
    ) {
        throw new AssertionError();
    }
}