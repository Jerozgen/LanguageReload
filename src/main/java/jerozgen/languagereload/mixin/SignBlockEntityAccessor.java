package jerozgen.languagereload.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
public interface SignBlockEntityAccessor {
    @Accessor
    void setTextsBeingEdited(OrderedText[] textsBeingEdited);
}
