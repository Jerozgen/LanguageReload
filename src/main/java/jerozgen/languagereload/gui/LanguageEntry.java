package jerozgen.languagereload.gui;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.ILanguageSelectScreen;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LanguageEntry extends LanguageList.Entry {
    private static final Component DEFAULT_LANGUAGE_TOOLTIP = Component.translatable("language.default.tooltip");

    private static final WidgetSprites ADD_TEXTURES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/add"),
            Identifier.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/add_highlighted"));
    private static final WidgetSprites REMOVE_TEXTURES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/remove"),
            Identifier.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/remove_highlighted"));
    private static final WidgetSprites MOVE_UP_TEXTURES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/move_up"),
            Identifier.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/move_up_highlighted"));
    private static final WidgetSprites MOVE_DOWN_TEXTURES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/move_down"),
            Identifier.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/move_down_highlighted"));

    private final Minecraft client = Minecraft.getInstance();

    private final String code;
    private final LanguageInfo language;
    private final LinkedList<String> selectedLanguages;
    private final Runnable refreshListsAction;

    private final List<AbstractWidget> buttons = new ArrayList<>();
    private final Button addButton = addButton(15, 24, ADD_TEXTURES, __ -> toggle());
    private final Button removeButton = addButton(15, 24, REMOVE_TEXTURES, __ -> toggle());
    private final Button moveUpButton = addButton(11, 11, MOVE_UP_TEXTURES, __ -> moveUp());
    private final Button moveDownButton = addButton(11, 11, MOVE_DOWN_TEXTURES, __ -> moveDown());

    public LanguageEntry(Runnable refreshListsAction, String code, LanguageInfo language, LinkedList<String> selectedLanguages) {
        this.code = code;
        this.language = language;
        this.selectedLanguages = selectedLanguages;
        this.refreshListsAction = refreshListsAction;
    }

    protected Button addButton(int width, int height, WidgetSprites textures, Button.OnPress action) {
        var button = new LanguageEntryButton(width, height, textures, action);
        button.visible = false;
        buttons.add(button);
        return button;
    }

    private boolean isDefault() {
        return code.equals(Language.DEFAULT);
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

    public void toggle() {
        if (isFocused()) {
            parentList.setFocused(null);
        }
        if (isSelected()) {
            selectedLanguages.remove(code);
        } else {
            selectedLanguages.addFirst(code);
        }
        refreshListsAction.run();
        ((ILanguageSelectScreen) parentList.getScreen()).languagereload_focusEntry(this);
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
    public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
        var x = this.getX();
        var y = this.getY();
        if (hovered || isFocused() || client.options.touchscreen().get()) {
            var x1 = x + 1;
            var y1 = y + 1;
            var x2 = parentList.getHoveredSelectionRight() - 1;
            var y2 = y + this.getHeight() - 1;
            graphics.fill(x1, y1, x2, y2, (hovered || isFocused()) ? 0xA0909090 : 0x50909090);
            buttons.forEach(button -> button.visible = false);
            extractButtons((button, buttonX, buttonY) -> {
                button.setX(buttonX);
                button.setY(buttonY);
                button.visible = true;
                button.extractRenderState(graphics, mouseX, mouseY, a);
            }, x, y);
            if ((hovered || isFocused()) && isDefault()) {
                extractDefaultLanguageTooltip(graphics, x, y);
            }
        }
        graphics.text(client.font, language.name(), x + 29, y + 3, CommonColors.WHITE);
        graphics.text(client.font, language.region(), x + 29, y + 14, CommonColors.GRAY);
    }

    private void extractButtons(ButtonExtractor extractor, int x, int y) {
        if (isSelected()) {
            if (!isDefault() || Config.getInstance().removableDefaultLanguage) extractor.extract(removeButton, x, y);
            if (!isFirst()) extractor.extract(moveUpButton, x + removeButton.getWidth() + 1, y);
            if (!isLast()) extractor.extract(moveDownButton, x + removeButton.getWidth() + 1, y + moveUpButton.getHeight() + 2);
        } else extractor.extract(addButton, x + 7, y);
    }

    private void extractDefaultLanguageTooltip(GuiGraphicsExtractor graphics, int x, int y) {
        var tooltip = client.font.split(DEFAULT_LANGUAGE_TOOLTIP, parentList.getRowWidth() - 6);
        ClientTooltipPositioner positioner = (_, screenHeight, _, _, width, height) -> {
            var pos = new Vector2i(
                    x + 3 + (parentList.getRowWidth() - width - 6) / 2,
                    y + parentList.getRowHeight() + 4);
            if (pos.y > parentList.getBottom() + 2 || pos.y + height + 5 > screenHeight)
                pos.y = y - height - 6;
            return pos;
        };
        graphics.setTooltipForNextFrame(client.font, tooltip, positioner, 0, 0, true);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.isConfirmation() && (!isDefault() || Config.getInstance().removableDefaultLanguage)) {
            this.toggle();
            return true;
        }
        if (input.hasShiftDown()) {
            if (input.isUp()) {
                this.moveUp();
                return true;
            }
            if (input.isDown()) {
                this.moveDown();
                return true;
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public Component getNarration() {
        return Component.translatable("narrator.select", language.toComponent());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        for (var widget : buttons)
            if (widget.mouseClicked(click, doubled)) {
                return true;
            }
        return false;
    }

    @Override
    public String getCode() {
        return code;
    }

    public LanguageInfo getLanguage() {
        return language;
    }

    @FunctionalInterface
    private interface ButtonExtractor {
        void extract(Button button, int x, int y);
    }
}
