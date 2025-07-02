package jerozgen.languagereload.gui;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.access.ILanguageSelectScreen;
import jerozgen.languagereload.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LanguageEntry extends ObjectSelectionList.Entry<LanguageEntry> {
    private static final Component DEFAULT_LANGUAGE_TOOLTIP = Component.translatable("language.default.tooltip");

    private static final WidgetSprites ADD_TEXTURES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/add"),
            ResourceLocation.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/add_highlighted"));
    private static final WidgetSprites REMOVE_TEXTURES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/remove"),
            ResourceLocation.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/remove_highlighted"));
    private static final WidgetSprites MOVE_UP_TEXTURES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/move_up"),
            ResourceLocation.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/move_up_highlighted"));
    private static final WidgetSprites MOVE_DOWN_TEXTURES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/move_down"),
            ResourceLocation.fromNamespaceAndPath(LanguageReload.MOD_ID, "language_selection/move_down_highlighted"));

    private final Minecraft client = Minecraft.getInstance();

    private final String code;
    private final LanguageInfo language;
    private final LinkedList<String> selectedLanguages;
    private final Runnable refreshListsAction;

    private final List<AbstractWidget> buttons = new ArrayList<>();
    private final Button moveUpButton = addButton(11, 11, MOVE_UP_TEXTURES, __ -> moveUp());
    private final Button moveDownButton = addButton(11, 11, MOVE_DOWN_TEXTURES, __ -> moveDown());
    private LanguageListWidget parentList;
    private final Button addButton = addButton(15, 24, ADD_TEXTURES, __ -> add());
    private final Button removeButton = addButton(15, 24, REMOVE_TEXTURES, __ -> remove());

    public LanguageEntry(Runnable refreshListsAction, String code, LanguageInfo language, LinkedList<String> selectedLanguages) {
        this.code = code;
        this.language = language;
        this.selectedLanguages = selectedLanguages;
        this.refreshListsAction = refreshListsAction;
    }

    protected Button addButton(int width, int height, WidgetSprites textures, Button.OnPress action) {
        var button = new ImageButton(0, 0, width, height, textures, action);
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
    public void render(@NotNull GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        x -= 2;
        y -= 2;
        if (hovered || isFocused() || client.options.touchscreen().get()) {
            var x1 = x + 1;
            var y1 = y + 1;
            var x2 = parentList.getHoveredSelectionRight() - 1;
            var y2 = y + entryHeight + 3;
            context.fill(x1, y1, x2, y2, (hovered || isFocused()) ? 0xA0909090 : 0x50909090);
            buttons.forEach(button -> button.visible = false);
            renderButtons((button, buttonX, buttonY) -> {
                button.setX(buttonX);
                button.setY(buttonY);
                button.visible = true;
                button.render(context, mouseX, mouseY, tickDelta);
            }, x, y);
            if ((hovered || isFocused()) && isDefault()) {
                renderDefaultLanguageTooltip(context, x, y);
            }
        }
        context.drawString(client.font, language.name(), x + 29, y + 3, CommonColors.WHITE);
        context.drawString(client.font, language.region(), x + 29, y + 14, CommonColors.GRAY);
    }

    private void renderButtons(ButtonRenderer renderer, int x, int y) {
        if (isSelected()) {
            if (!isDefault() || Config.getInstance().removableDefaultLanguage) renderer.render(removeButton, x, y);
            if (!isFirst()) renderer.render(moveUpButton, x + removeButton.getWidth() + 1, y);
            if (!isLast()) renderer.render(moveDownButton, x + removeButton.getWidth() + 1, y + moveUpButton.getHeight() + 2);
        } else renderer.render(addButton, x + 7, y);
    }

    private void renderDefaultLanguageTooltip(GuiGraphics context, int x, int y) {
        var tooltip = client.font.split(DEFAULT_LANGUAGE_TOOLTIP, parentList.getRowWidth() - 6);
        ClientTooltipPositioner positioner = (screenWidth, screenHeight, mouseX, mouseY, width, height) -> {
            var pos = new Vector2i(
                    x + 3 + (parentList.getRowWidth() - width - 6) / 2,
                    y + parentList.getRowHeight() + 4);
            if (pos.y > parentList.getBottom() + 2 || pos.y + height + 5 > screenHeight)
                pos.y = y - height - 6;
            return pos;
        };
        context.setTooltipForNextFrame(client.font, tooltip, positioner, 0, 0, true);
    }

    @Override
    public @NotNull Component getNarration() {
        return Component.translatable("narrator.select", language.toComponent());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var widget : buttons)
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                ((ILanguageSelectScreen) parentList.getScreen()).languagereload_focusList(parentList);
                return true;
            }
        return false;
    }

    public LanguageListWidget getParent() {
        return parentList;
    }

    public void setParent(LanguageListWidget list) {
        this.parentList = list;
    }

    public String getCode() {
        return code;
    }

    public LanguageInfo getLanguage() {
        return language;
    }

    @FunctionalInterface
    private interface ButtonRenderer {
        void render(Button button, int x, int y);
    }
}
