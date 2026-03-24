package jerozgen.languagereload.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;

public class LanguageEntryButton extends ImageButton {
    public LanguageEntryButton(int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(0, 0, width, height, sprites, onPress);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        if (this.isHovered()) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }
}
