package jerozgen.languagereload.access;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IAdvancementsScreen {
    void languagereload_recreateWidgets();
}