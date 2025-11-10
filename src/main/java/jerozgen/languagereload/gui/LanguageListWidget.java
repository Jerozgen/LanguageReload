package jerozgen.languagereload.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class LanguageListWidget extends AlwaysSelectedEntryListWidget<LanguageListWidget.Entry> {
    private final HeaderEntry headerEntry;
    private final LanguageOptionsScreen screen;

    public LanguageListWidget(MinecraftClient client, LanguageOptionsScreen screen, int width, int height, Text title) {
        super(client, width, height - 83 - 16, 32 + 16, 24);
        this.screen = screen;
        this.headerEntry = new HeaderEntry(
                client.textRenderer,
                Text.empty().append(title).formatted(Formatting.UNDERLINE, Formatting.BOLD)
        );
        centerListVertically = false;
    }

    public void set(Stream<? extends Entry> entries) {
        this.clearEntries();
        this.addEntry(headerEntry, (int) (9f * 1.5f));
        entries.forEach(this::addEntry);
        this.refreshScroll();
    }

    @Override
    protected int addEntry(Entry entry, int entryHeight) {
        entry.setParent(this);
        return super.addEntry(entry, entryHeight);
    }

    // Remove hovering in scrollbar area
    @Override
    @Nullable
    protected Entry getEntryAtPosition(double x, double y) {
        var entry = super.getEntryAtPosition(x, y);
        return entry != null && this.overflows() && x >= this.getScrollbarX()
                ? null
                : entry;
    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, Entry entry, int color) {
        if (this.overflows()) {
            var x1 = entry.getX();
            var y1 = entry.getY();
            var x2 = this.getScrollbarX();
            var y2 = y1 + entry.getHeight();
            context.fill(x1, y1, x2, y2, color);
            context.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, Colors.BLACK);
        } else {
            super.drawSelectionHighlight(context, entry, color);
        }
    }

    public int getHoveredSelectionRight() {
        return this.overflows()
                ? this.getScrollbarX()
                : this.getRowRight();
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

    @Override
    public boolean keyPressed(KeyInput input) {
        var selectedEntry = this.getSelectedOrNull();
        return selectedEntry != null
                ? selectedEntry.keyPressed(input)
                : super.keyPressed(input);
    }

    public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry<LanguageListWidget.Entry> {
        protected LanguageListWidget parentList;

        public void setParent(LanguageListWidget list) {
            this.parentList = list;
        }

        public LanguageListWidget getParent() {
            return parentList;
        }

        public abstract String getCode();
    }
}
