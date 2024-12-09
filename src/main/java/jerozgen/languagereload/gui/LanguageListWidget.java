package jerozgen.languagereload.gui;

import jerozgen.languagereload.access.ILanguageOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

public class LanguageListWidget extends AlwaysSelectedEntryListWidget<LanguageEntry> {
    private final Text title;
    private final LanguageOptionsScreen screen;

    public LanguageListWidget(MinecraftClient client, LanguageOptionsScreen screen, int width, int height, Text title) {
        super(client, width, height - 83 - 16, 32 + 16, 24, (int) (9f * 1.5f));
        this.title = title;
        this.screen = screen;
        centerListVertically = false;
    }

    @Override
    protected void renderHeader(DrawContext context, int x, int y) {
        var headerText = title.copy().formatted(Formatting.UNDERLINE, Formatting.BOLD);
        int headerPosX = x + width / 2 - client.textRenderer.getWidth(headerText) / 2;
        int headerPosY = Math.min(this.getY() + 3, y);
        context.drawTextWithShadow(client.textRenderer, headerText, headerPosX, headerPosY, Colors.WHITE);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var selectedEntry = this.getSelectedOrNull();
        if (selectedEntry == null) return super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == GLFW_KEY_SPACE || keyCode == GLFW_KEY_ENTER) {
            selectedEntry.toggle();
            this.setFocused(null);
            ((ILanguageOptionsScreen) screen).languagereload_focusEntry(selectedEntry);
            return true;
        }

        if (Screen.hasShiftDown()) {
            if (keyCode == GLFW_KEY_DOWN) {
                selectedEntry.moveDown();
                return true;
            }
            if (keyCode == GLFW_KEY_UP) {
                selectedEntry.moveUp();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Remove hovering in scrollbar area
    @Override
    @Nullable
    protected LanguageEntry getEntryAtPosition(double x, double y) {
        var entry = super.getEntryAtPosition(x, y);
        return entry != null && this.overflows() && x >= this.getScrollbarX()
                ? null
                : entry;
    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        if (this.overflows()) {
            var x1 = this.getRowLeft() - 2;
            var x2 = this.getScrollbarX();
            var y1 = y - 2;
            var y2 = y + entryHeight + 2;
            context.fill(x1, y1, x2, y2, borderColor);
            context.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillColor);
        } else {
            super.drawSelectionHighlight(context, y, entryWidth, entryHeight, borderColor, fillColor);
        }
    }

    public int getHoveredSelectionRight() {
        return this.overflows()
                ? this.getScrollbarX()
                : this.getRowRight() - 2;
    }

    public LanguageOptionsScreen getScreen() {
        return screen;
    }

    public int getRowHeight() {
        return itemHeight;
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarX() {
        return this.getRight() - 6;
    }
}
