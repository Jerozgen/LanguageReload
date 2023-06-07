package jerozgen.languagereload.mixin;

import net.minecraft.block.entity.SignText;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignText.class)
public interface SignTextAccessor {
    @Accessor("orderedMessages")
    void languagereload_setOrderedMessages(OrderedText[] orderedMessages);
}
