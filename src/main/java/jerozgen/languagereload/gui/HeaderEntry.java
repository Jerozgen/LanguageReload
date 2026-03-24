package jerozgen.languagereload.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class HeaderEntry extends LanguageList.Entry {
    private final Font font;
    private final Component text;

    public HeaderEntry(Font font, Component text) {
        this.font = font;
        this.text = text;
    }

    @Override
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        var x = this.getX() + this.getWidth() / 2;
        var y = this.getContentYMiddle() - 9 / 2;
        graphics.centeredText(font, text, x, y, CommonColors.WHITE);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        return false;
    }

    @Override
    public Component getNarration() {
        return text;
    }

    @Override
    public String getCode() {
        return "";
    }
}
