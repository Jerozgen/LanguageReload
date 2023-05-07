package jerozgen.languagereload.mixin;

import net.minecraft.client.gui.components.ObjectSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ObjectSelectionList.class)
public interface AlwaysSelectedEntryListWidgetAccessor {
    @Accessor
    void setInFocus(boolean value);
}