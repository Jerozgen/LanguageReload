package jerozgen.languagereload.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class LanguageList extends ObjectSelectionList<LanguageList.Entry> {
    private final HeaderEntry headerEntry;
    private final LanguageSelectScreen screen;

    public LanguageList(Minecraft minecraft, LanguageSelectScreen screen, int width, int height, Component title) {
        super(minecraft, width, height - 83 - 16, 32 + 16, 24);
        this.screen = screen;
        this.headerEntry = new HeaderEntry(
                minecraft.font,
                Component.empty().append(title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD)
        );
        centerListVertically = false;
    }

    public void set(Stream<? extends Entry> entries) {
        this.clearEntries();
        this.addEntry(headerEntry, (int) (9f * 1.5f));
        entries.forEach(this::addEntry);
        this.refreshScrollAmount();
    }

    @Override
    protected int addEntry(Entry entry, int height) {
        entry.setParent(this);
        return super.addEntry(entry, height);
    }

    // Remove hovering in scrollbar area
    @Override
    @Nullable
    protected Entry getEntryAtPosition(double posX, double posY) {
        var entry = super.getEntryAtPosition(posX, posY);
        return entry != null && this.scrollbarVisible() && posX >= this.scrollBarX()
                ? null
                : entry;
    }

    @Override
    protected void renderSelection(GuiGraphics graphics, Entry entry, int color) {
        if (this.scrollbarVisible()) {
            var x1 = entry.getX();
            var y1 = entry.getY();
            var x2 = this.scrollBarX();
            var y2 = y1 + entry.getHeight();
            graphics.fill(x1, y1, x2, y2, color);
            graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, CommonColors.BLACK);
        } else {
            super.renderSelection(graphics, entry, color);
        }
    }

    public int getHoveredSelectionRight() {
        return this.scrollbarVisible()
                ? this.scrollBarX()
                : this.getRowRight();
    }

    public LanguageSelectScreen getScreen() {
        return screen;
    }

    public int getRowHeight() {
        return defaultEntryHeight;
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int scrollBarX() {
        return this.getRight() - 6;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        var selectedEntry = this.getSelected();
        return selectedEntry != null
                ? selectedEntry.keyPressed(event)
                : super.keyPressed(event);
    }

    public abstract static class Entry extends ObjectSelectionList.Entry<LanguageList.Entry> {
        protected LanguageList parentList;

        public void setParent(LanguageList list) {
            this.parentList = list;
        }

        public LanguageList getParent() {
            return parentList;
        }

        public abstract String getCode();
    }
}
