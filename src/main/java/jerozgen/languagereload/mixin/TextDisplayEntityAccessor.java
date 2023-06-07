package jerozgen.languagereload.mixin;

import net.minecraft.entity.decoration.DisplayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DisplayEntity.TextDisplayEntity.class)
public interface TextDisplayEntityAccessor {
    @Accessor("textLines")
    void languagereload_setTextLines(DisplayEntity.TextDisplayEntity.TextLines textLines);
}
