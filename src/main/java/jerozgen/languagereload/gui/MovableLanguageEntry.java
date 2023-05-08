package jerozgen.languagereload.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.LanguageInfo;

import java.util.LinkedList;

public class MovableLanguageEntry extends LanguageEntry {
    private final Button addButton = addButton(15, 24, 0, 0, __ -> add());
    private final Button removeButton = addButton(15, 24, 15, 0, __ -> remove());
    private final Button moveUpButton = addButton(11, 11, 31, 0, __ -> moveUp());
    private final Button moveDownButton = addButton(11, 11, 31, 13, __ -> moveDown());

    public MovableLanguageEntry(Runnable refreshListsAction, String code, LanguageInfo language, LinkedList<String> selectedLanguages) {
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
        selectedLanguages.add(code);
        refreshListsAction.run();
    }

    private void remove() {
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