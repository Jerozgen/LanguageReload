package jerozgen.languagereload.mixin;

import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AlwaysSelectedEntryListWidget.class)
public interface AlwaysSelectedEntryListWidgetAccessor {
    @Accessor
    void setInFocus(boolean value);
}
