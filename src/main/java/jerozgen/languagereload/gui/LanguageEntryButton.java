package jerozgen.languagereload.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;

public class LanguageEntryButton extends ImageButton {
    public LanguageEntryButton(int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(0, 0, width, height, sprites, onPress);
    }

    @Override
    public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        super.renderContents(graphics, mouseX, mouseY, deltaTicks);
        if (this.isHovered()) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }
}
