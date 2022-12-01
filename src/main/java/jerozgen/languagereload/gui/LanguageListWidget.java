package jerozgen.languagereload.gui;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LanguageListWidget extends ElementListWidget<LanguageEntry> {
    private final Text title;

    public LanguageListWidget(MinecraftClient client, int width, int height, Text title) {
        super(client, width, height, 48, height - 55 + 4, 24);
        this.title = title;

        setRenderHeader(true, (int) (9.0f * 1.5f));
        centerListVertically = false;
    }

    @Override
    protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator) {
        var headerText = title.copy().formatted(Formatting.UNDERLINE, Formatting.BOLD);
        int headerPosX = x + width / 2 - client.textRenderer.getWidth(headerText) / 2;
        int headerPosY = Math.min(top + 3, y);
        client.textRenderer.draw(matrices, headerText, headerPosX, headerPosY, 0xFFFFFF);
    }

    @Override
    public int getRowTop(int index) {
        return super.getRowTop(index);
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarPositionX() {
        return right - 6;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        var hoveredEntry = getHoveredEntry();
        if (hoveredEntry != null) {
            appendNarrations(builder.nextMessage(), hoveredEntry);
            hoveredEntry.appendNarrations(builder);
        }
    }
}
