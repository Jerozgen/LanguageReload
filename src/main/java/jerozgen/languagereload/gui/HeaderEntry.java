package jerozgen.languagereload.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class HeaderEntry extends LanguageList.Entry {
    private final Font textRenderer;
    private final Component text;

    public HeaderEntry(Font textRenderer, Component text) {
        this.textRenderer = textRenderer;
        this.text = text;
    }

    @Override
    public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        var x = this.getX() + this.getWidth() / 2;
        var y = this.getContentYMiddle() - 9 / 2;
        context.drawCenteredString(textRenderer, text, x, y, CommonColors.WHITE);
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
