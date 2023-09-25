package jerozgen.languagereload.gui;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public class MovableLanguageEntry extends LanguageEntry {
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

    private final ButtonWidget addButton = addButton(15, 24, ADD_TEXTURES, __ -> add());
    private final ButtonWidget removeButton = addButton(15, 24, REMOVE_TEXTURES, __ -> remove());
    private final ButtonWidget moveUpButton = addButton(11, 11, MOVE_UP_TEXTURES, __ -> moveUp());
    private final ButtonWidget moveDownButton = addButton(11, 11, MOVE_DOWN_TEXTURES, __ -> moveDown());

    public MovableLanguageEntry(Runnable refreshListsAction, String code, LanguageDefinition language, LinkedList<String> selectedLanguages) {
        super(refreshListsAction, code, language, selectedLanguages);
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
        selectedLanguages.add(code);
        refreshListsAction.run();
    }

    private void remove() {
        if (isFocused())
            parentList.setFocused(null);
        selectedLanguages.remove(code);
        refreshListsAction.run();
    }

    @Override
    public void toggle() {
        if (!isSelected()) add();
        else remove();
    }

    @Override
    public void moveUp() {
        if (!isSelected()) return;
        if (isFirst()) return;
        var index = selectedLanguages.indexOf(code);
        selectedLanguages.add(index - 1, selectedLanguages.remove(index));
        refreshListsAction.run();
    }

    @Override
    public void moveDown() {
        if (!isSelected()) return;
        if (isLast()) return;
        var index = selectedLanguages.indexOf(code);
        selectedLanguages.add(index + 1, selectedLanguages.remove(index));
        refreshListsAction.run();
    }

    @Override
    protected void renderButtons(ButtonRenderer renderer, int x, int y) {
        if (isSelected()) {
            renderer.render(removeButton, x, y);
            if (!isFirst()) renderer.render(moveUpButton, x + removeButton.getWidth() + 1, y);
            if (!isLast()) renderer.render(moveDownButton, x + removeButton.getWidth() + 1, y + moveUpButton.getHeight() + 2);
        } else renderer.render(addButton, x + 7, y);
    }
}
