package jerozgen.languagereload.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class HeaderEntry extends LanguageListWidget.Entry {
    private final TextRenderer textRenderer;
    private final Text text;

    public HeaderEntry(TextRenderer textRenderer, Text text) {
        this.textRenderer = textRenderer;
        this.text = text;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        var x = this.getX() + this.getWidth() / 2;
        var y = this.getContentMiddleY() - 9 / 2;
        context.drawCenteredTextWithShadow(textRenderer, text, x, y, Colors.WHITE);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return false;
    }

    @Override
    public Text getNarration() {
        return text;
    }

    @Override
    public String getCode() {
        return "";
    }
}
