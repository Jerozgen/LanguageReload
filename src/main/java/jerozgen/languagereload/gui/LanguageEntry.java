package jerozgen.languagereload.gui;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class LanguageEntry extends ElementListWidget.Entry<LanguageEntry> {
    protected static final Identifier TEXTURE = new Identifier(LanguageReload.MOD_ID, "textures/gui/language_selection.png");
    protected static final int TEXTURE_WIDTH = 64;
    protected static final int TEXTURE_HEIGHT = 64;
    protected static final int HOVERED_V_OFFSET = 24;

    private final static int SCROLLBAR_WIDTH = 6;
    private final static int LEFT_MARGIN = 2;
    private final static int ENTRY_HEIGHT = 24;

    protected final MinecraftClient client = MinecraftClient.getInstance();
    protected final LanguageDefinition language;
    protected final LinkedList<LanguageDefinition> selectedLanguages;
    protected final Runnable refreshListsAction;

    private final List<ClickableWidget> buttons = new ArrayList<>();

    public LanguageEntry(Runnable refreshListsAction, LanguageDefinition language, LinkedList<LanguageDefinition> selectedLanguages) {
        this.language = language;
        this.selectedLanguages = selectedLanguages;
        this.refreshListsAction = refreshListsAction;
    }

    protected ButtonWidget addChild(ButtonWidget button) {
        button.visible = false;
        buttons.add(button);
        return button;
    }

    protected abstract void renderButtons(ButtonRenderer buttonRenderer, int top, int left);

    public LanguageDefinition getLanguage() {
        return language;
    }

    @Override
    public void render(MatrixStack matrices, int index, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (client.options.getTouchscreen().getValue() || hovered) {
            if (hovered) {
                var right = left + entryWidth - SCROLLBAR_WIDTH - LEFT_MARGIN;
                DrawableHelper.fill(matrices, left, top, right, top + ENTRY_HEIGHT, 0xA0909090);
            }
            buttons.forEach(button -> button.visible = false);
            ButtonRenderer buttonRenderer = (button, x, y) -> {
                button.setX(x);
                button.setY(y);
                button.visible = true;
                button.render(matrices, mouseX, mouseY, tickDelta);
            };
            renderButtons(buttonRenderer, top, left);
        }
        client.textRenderer.drawWithShadow(matrices, language.getName(), left + 29, top + 3, 0xFFFFFF);
        client.textRenderer.drawWithShadow(matrices, language.getRegion(), left + 29, top + 14, 0x808080);
    }

    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, Text.translatable("narrator.select", language));
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return false;
    }

    @Override
    public List<? extends Element> children() {
        return buttons;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return buttons;
    }

    @FunctionalInterface
    protected interface ButtonRenderer {
        void render(ButtonWidget button, int x, int y);
    }
}
