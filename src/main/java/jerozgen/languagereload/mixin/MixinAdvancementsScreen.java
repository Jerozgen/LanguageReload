package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.IAdvancementsScreen;
import jerozgen.languagereload.access.IAdvancementsTab;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
public abstract class MixinAdvancementsScreen extends Screen implements ClientAdvancementManager.Listener, IAdvancementsScreen {
    @Shadow @Final private Map<Advancement, AdvancementTab> tabs;

    protected MixinAdvancementsScreen(Text title) {
        super(title);
    }

    @Override
    public void recreateWidgets() {
        for (AdvancementTab tab : tabs.values()) {
            ((IAdvancementsTab) tab).recreateWidgets();
        }
    }
}
