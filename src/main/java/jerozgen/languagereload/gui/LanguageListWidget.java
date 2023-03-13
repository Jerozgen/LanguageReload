package jerozgen.languagereload.gui;


import jerozgen.languagereload.access.ILanguageOptionsScreen;
import jerozgen.languagereload.mixin.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import static org.lwjgl.glfw.GLFW.*;

public class LanguageListWidget extends AlwaysSelectedEntryListWidget<LanguageEntry> {
    private final Text title;
    private final LanguageOptionsScreen screen;

    public LanguageListWidget(MinecraftClient client, LanguageOptionsScreen screen, int width, int height, Text title) {
        super(client, width, height, 48, height - 55 + 4, 24);
        this.title = title;
        this.screen = screen;

        setRenderHeader(true, (int) (9f * 1.5f));
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var selectedEntry = this.getSelectedOrNull();
        if (selectedEntry == null) return super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == GLFW_KEY_SPACE || keyCode == GLFW_KEY_ENTER) {
            selectedEntry.toggle();
            this.setFocused(null);
            ((ILanguageOptionsScreen) screen).focusEntry(selectedEntry);
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

        return ((EntryListWidgetAccessor) this).isScrolling();
    }

    @Override
    @Nullable
    protected LanguageEntry getEntryAtPosition(double x, double y) {
        int halfRowWidth = this.getRowWidth() / 2;
        int center = left + width / 2;
        int minX = center - halfRowWidth;
        int maxX = center + halfRowWidth;
        var scrollbarPositionX = this.getScrollbarPositionX();
        int m = MathHelper.floor(y - top) - headerHeight + (int) this.getScrollAmount() - 4 + 2;
        int entryIndex = m / itemHeight;
        return (x < scrollbarPositionX && x >= minX && x <= maxX && entryIndex >= 0 && m >= 0
                && entryIndex < this.getEntryCount() ? this.children().get(entryIndex) : null);
    }

    public LanguageOptionsScreen getScreen() {
        return screen;
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarPositionX() {
        return right - 6;
    }

    protected boolean isFocused() {
        return screen.getFocused() == this;
    }
}
