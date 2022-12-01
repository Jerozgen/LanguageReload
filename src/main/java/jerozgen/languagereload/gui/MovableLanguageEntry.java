package jerozgen.languagereload.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.LanguageDefinition;

import java.util.LinkedList;

public class MovableLanguageEntry extends LanguageEntry {
    private final ButtonWidget addButton = addChild(createButton(15, 24, 0, 0, __ -> {
        selectedLanguages.add(language);
    }));

    private final ButtonWidget removeButton = addChild(createButton(15, 24, 15, 0, __ -> {
        selectedLanguages.remove(language);
    }));

    private final ButtonWidget moveUpButton = addChild(createButton(11, 11, 31, 0, __ -> {
        var index = selectedLanguages.indexOf(language);
        selectedLanguages.add(index - 1, selectedLanguages.remove(index));
    }));

    private final ButtonWidget moveDownButton = addChild(createButton(11, 11, 31, 12, __ -> {
        var index = selectedLanguages.indexOf(language);
        selectedLanguages.add(index + 1, selectedLanguages.remove(index));
    }));

    public MovableLanguageEntry(Runnable refreshListsAction, LanguageDefinition language, LinkedList<LanguageDefinition> selectedLanguages) {
        super(refreshListsAction, language, selectedLanguages);
    }

    private ButtonWidget createButton(int width, int height, int u, int v, ButtonWidget.PressAction pressAction) {
        return new TexturedButtonWidget(0, 0, width, height, u, v, HOVERED_V_OFFSET, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, button -> {
            pressAction.onPress(button);
            this.refreshListsAction.run();
        });
    }

    @Override
    protected void renderButtons(ButtonRenderer buttonRenderer, int top, int left) {
        if (selectedLanguages.contains(language)) {
            buttonRenderer.render(removeButton, left, top);
            var mainLanguage = selectedLanguages.peekFirst();
            if (!language.equals(mainLanguage)) {
                buttonRenderer.render(moveUpButton, left + removeButton.getWidth() + 1, top);
            }
            var lastFallback = selectedLanguages.peekLast();
            if (!language.equals(lastFallback)) {
                buttonRenderer.render(moveDownButton, left + removeButton.getWidth() + 1, top + moveUpButton.getHeight() + 2);
            }
        } else buttonRenderer.render(addButton, left + 7, top);
    }
}
