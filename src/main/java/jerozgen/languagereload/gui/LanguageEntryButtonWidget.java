package jerozgen.languagereload.gui;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;

public class LanguageEntryButtonWidget extends TexturedButtonWidget {
    public LanguageEntryButtonWidget(int width, int height, ButtonTextures textures, PressAction action) {
        super(0, 0, width, height, textures, action);
    }
}
