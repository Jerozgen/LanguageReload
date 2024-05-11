package jerozgen.languagereload.gui;

import jerozgen.languagereload.access.ILanguageOptionsScreen;
import jerozgen.languagereload.mixin.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import static org.lwjgl.glfw.GLFW.*;

public class LanguageListWidget extends AlwaysSelectedEntryListWidget<LanguageEntry> {
    private final Text title;
    private final LanguageOptionsScreen screen;

    public LanguageListWidget(MinecraftClient client, LanguageOptionsScreen screen, int width, int height, Text title) {
        super(client, width, height - 83 - 16, 32 + 16, 24);
        this.title = title;
        this.screen = screen;

        setRenderHeader(true, (int) (9f * 1.5f));
        centerListVertically = false;
    }

    @Override
    protected void renderHeader(DrawContext context, int x, int y) {
        var headerText = title.copy().formatted(Formatting.UNDERLINE, Formatting.BOLD);
        int headerPosX = x + width / 2 - client.textRenderer.getWidth(headerText) / 2;
        int headerPosY = Math.min(this.getY() + 3, y);
        context.drawText(client.textRenderer, headerText, headerPosX, headerPosY, 0xFFFFFF, false);
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

    // Remove focusing on entry click
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) return false;

        var entry = this.getEntryAtPosition(mouseX, mouseY);
        if (entry == null && button == 0) return true;

        if (entry != null && entry.mouseClicked(mouseX, mouseY, button)) {
            var focusedEntry = this.getFocused();
            if (focusedEntry != entry && focusedEntry instanceof ParentElement parentElement)
                parentElement.setFocused(null);
            this.setDragging(true);
            return true;
        }

        return ((EntryListWidgetAccessor) this).languagereload_isScrolling();
    }

    @Override
    @Nullable
    protected LanguageEntry getEntryAtPosition(double x, double y) {
        int halfRowWidth = this.getRowWidth() / 2;
        int center = this.getX() + width / 2;
        int minX = center - halfRowWidth;
        int maxX = center + halfRowWidth;
        int m = MathHelper.floor(y - this.getY()) - headerHeight + (int) this.getScrollAmount() - 4 + 2;
        int entryIndex = m / itemHeight;
        var hasScrollbar = this.isScrollbarVisible();
        var scrollbarX = this.getScrollbarX();
        var entryCount = this.getEntryCount();
        return x >= minX && x <= maxX && (!hasScrollbar || x < scrollbarX) && entryIndex >= 0 && m >= 0 && entryIndex < entryCount
                ? this.children().get(entryIndex)
                : null;
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

    public void updateScroll() {
        this.setScrollAmount(this.getScrollAmount());
    }
}
