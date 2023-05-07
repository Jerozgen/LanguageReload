package jerozgen.languagereload.mixin;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
public interface SignBlockEntityAccessor {
    @Accessor("renderMessages")
    void languagereload_setTextsBeingEdited(FormattedCharSequence[] textsBeingEdited);
}