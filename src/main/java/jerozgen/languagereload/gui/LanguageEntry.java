package jerozgen.languagereload.gui;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.ILanguageOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LanguageEntry extends AlwaysSelectedEntryListWidget.Entry<LanguageEntry> {
    private static final Text DEFAULT_LANGUAGE_TOOLTIP = Text.translatable("language.default.tooltip");

    private static final ButtonTextures ADD_TEXTURES = new ButtonTextures(
            new Identifier(LanguageReload.MOD_ID, "language_selection/add"),
            new Identifier(LanguageReload.MOD_ID, "language_selection/add_highlighted"));
    private static final ButtonTextures REMOVE_TEXTURES = new ButtonTextures(
            new Identifier(LanguageReload.MOD_ID, "language_selection/remove"),
            new Identifier(LanguageReload.MOD_ID, "language_selection/remove_highlighted"));
    private static final ButtonTextures MOVE_UP_TEXTURES = new ButtonTextures(
            new Identifier(LanguageReload.MOD_ID, "language_selection/move_up"),
            new Identifier(LanguageReload.MOD_ID, "language_selection/move_up_highlighted"));
    private static final ButtonTextures MOVE_DOWN_TEXTURES = new ButtonTextures(
            new Identifier(LanguageReload.MOD_ID, "language_selection/move_down"),
            new Identifier(LanguageReload.MOD_ID, "language_selection/move_down_highlighted"));

    private final MinecraftClient client = MinecraftClient.getInstance();

    private final String code;
    private final LanguageDefinition language;
    private final LinkedList<String> selectedLanguages;
    private final Runnable refreshListsAction;

    private final List<ClickableWidget> buttons = new ArrayList<>();
    private final ButtonWidget addButton = addButton(15, 24, ADD_TEXTURES, __ -> add());
    private final ButtonWidget removeButton = addButton(15, 24, REMOVE_TEXTURES, __ -> remove());
    private final ButtonWidget moveUpButton = addButton(11, 11, MOVE_UP_TEXTURES, __ -> moveUp());
    private final ButtonWidget moveDownButton = addButton(11, 11, MOVE_DOWN_TEXTURES, __ -> moveDown());

    private LanguageListWidget parentList;

    public LanguageEntry(Runnable refreshListsAction, String code, LanguageDefinition language, LinkedList<String> selectedLanguages) {
        this.code = code;
        this.language = language;
        this.selectedLanguages = selectedLanguages;
        this.refreshListsAction = refreshListsAction;
    }

    protected ButtonWidget addButton(int width, int height, ButtonTextures textures, ButtonWidget.PressAction action) {
        var button = new TexturedButtonWidget(0, 0, width, height, textures, action);
        button.visible = false;
        buttons.add(button);
        return button;
    }

    private boolean isDefault() {
        return code.equals(Language.DEFAULT_LANGUAGE);
    }

    private boolean isSelected() {
        return selectedLanguages.contains(code);
    }

    private boolean isFirst() {
        return code.equals(selectedLanguages.peekFirst());
    }

    private boolean isLast() {
        return code.equals(selectedLanguages.peekLast());
    }

    private void add() {
        if (isFocused())
            parentList.setFocused(null);
        selectedLanguages.addFirst(code);
        refreshListsAction.run();
    }

    private void remove() {
        if (isFocused())
            parentList.setFocused(null);
        selectedLanguages.remove(code);
        refreshListsAction.run();
    }

    public void toggle() {
        if (!isSelected()) add();
        else remove();
    }

    public void moveUp() {
        if (!isSelected()) return;
        if (isFirst()) return;
        var index = selectedLanguages.indexOf(code);
        selectedLanguages.add(index - 1, selectedLanguages.remove(index));
        refreshListsAction.run();
    }

    public void moveDown() {
        if (!isSelected()) return;
        if (isLast()) return;
        var index = selectedLanguages.indexOf(code);
        selectedLanguages.add(index + 1, selectedLanguages.remove(index));
        refreshListsAction.run();
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        x -= 2;
        y -= 2;
        if (hovered || isFocused() || client.options.getTouchscreen().getValue()) {
            context.fill(x + 1, y + 1, x + entryWidth - 1, y + entryHeight + 3,
                    (hovered || isFocused()) ? 0xA0909090 : 0x50909090);
            buttons.forEach(button -> button.visible = false);
            renderButtons((button, buttonX, buttonY) -> {
                button.setX(buttonX);
                button.setY(buttonY);
                button.visible = true;
                button.render(context, mouseX, mouseY, tickDelta);
            }, x, y);
            if ((hovered || isFocused()) && isDefault())
                renderDefaultLanguageTooltip(x, y);
        }
        context.drawTextWithShadow(client.textRenderer, language.name(), x + 29, y + 3, 0xFFFFFF);
        context.drawTextWithShadow(client.textRenderer, language.region(), x + 29, y + 14, 0x808080);
    }

    private void renderButtons(ButtonRenderer renderer, int x, int y) {
        if (isSelected()) {
            renderer.render(removeButton, x, y);
            if (!isFirst()) renderer.render(moveUpButton, x + removeButton.getWidth() + 1, y);
            if (!isLast()) renderer.render(moveDownButton, x + removeButton.getWidth() + 1, y + moveUpButton.getHeight() + 2);
        } else renderer.render(addButton, x + 7, y);
    }

    private void renderDefaultLanguageTooltip(int x, int y) {
        var tooltip = client.textRenderer.wrapLines(DEFAULT_LANGUAGE_TOOLTIP, parentList.getRowWidth() - 6);
        parentList.getScreen().setTooltip(tooltip, (screenWidth, screenHeight, mouseX, mouseY, width, height) -> {
            var pos = new Vector2i(
                    x + 3 + (parentList.getRowWidth() - width - 6) / 2,
                    y + parentList.getRowHeight() + 4);
            if (pos.y > parentList.getBottom() + 2 || pos.y + height + 5 > screenHeight)
                pos.y = y - height - 6;
            return pos;
        }, true);
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", language.getDisplayText());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var widget : buttons)
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                ((ILanguageOptionsScreen) parentList.getScreen()).languagereload_focusList(parentList);
                return true;
            }
        return false;
    }

    public void setParent(LanguageListWidget list) {
        this.parentList = list;
    }

    public LanguageListWidget getParent() {
        return parentList;
    }

    public String getCode() {
        return code;
    }

    public LanguageDefinition getLanguage() {
        return language;
    }

    @FunctionalInterface
    private interface ButtonRenderer {
        void render(ButtonWidget button, int x, int y);
    }
}
