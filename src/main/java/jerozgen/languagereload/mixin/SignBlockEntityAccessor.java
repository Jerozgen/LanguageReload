package jerozgen.languagereload.mixin;

import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
public interface SignBlockEntityAccessor {

    @Accessor("frontText")
    SignText languagereload_getFrontText();

    @Accessor("backText")
    SignText languagereload_getBackText();
}