package jerozgen.languagereload.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import jerozgen.languagereload.gui.LanguageListWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin <E extends EntryListWidget.Entry<E>> extends ContainerWidget {

    public EntryListWidgetMixin(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    @Unique
    private static final int HEADER_HEIGHT = (int) (9f * 1.5f);
    
    @Shadow
    public abstract int getRowLeft();

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderList(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        // copy from yarn mapping 1.21.6+build.1
        if ((Object) this instanceof LanguageListWidget languageListWidget) {
            int i = this.getRowLeft();
            int j = this.getY() + 4 - (int) this.getScrollY();
            languageListWidget.renderHeader(context, i, j);
        }
    }

    @ModifyReturnValue(method = "getYOfFirstEntry", at = @At("RETURN"))
    private int getYOfFirstEntry(int original) {
        if ((Object) this instanceof LanguageListWidget) {
            return original + HEADER_HEIGHT;
        }
        return original;
    }

    @ModifyReturnValue(method = "getContentsHeightWithPadding", at = @At("RETURN"))
    private int getContentsHeightWithPadding(int original) {
        if ((Object) this instanceof LanguageListWidget) {
            return original + HEADER_HEIGHT;
        }
        return original;
    }
}
