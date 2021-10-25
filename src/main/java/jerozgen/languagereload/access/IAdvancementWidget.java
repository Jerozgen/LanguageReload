package jerozgen.languagereload.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface IAdvancementWidget {
    AdvancementTab getTab();

    AdvancementProgress getProgress();

    AdvancementWidget getParent();

    void setParent(AdvancementWidget parent);

    List<AdvancementWidget> getChildren();

    void setChildren(List<AdvancementWidget> children);
}
