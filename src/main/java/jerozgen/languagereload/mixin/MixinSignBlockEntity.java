package jerozgen.languagereload.mixin;

import jerozgen.languagereload.access.ISignBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SignBlockEntity.class)
public abstract class MixinSignBlockEntity extends BlockEntity implements ISignBlockEntity {
    @Shadow @Nullable private OrderedText[] textsBeingEdited;

    public MixinSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void clearCache() {
        textsBeingEdited = null;
    }
}
