package jerozgen.languagereload.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;

public class LanguageEntryButtonWidget extends TexturedButtonWidget {
    public LanguageEntryButtonWidget(int width, int height, ButtonTextures textures, PressAction action) {
        super(0, 0, width, height, textures, action);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
        if (this.isHovered()) {
            context.setCursor(StandardCursors.POINTING_HAND);
        }
    }
}
