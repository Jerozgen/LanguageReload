package jerozgen.languagereload.gui;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;

import java.util.LinkedList;

public class LockedLanguageEntry extends LanguageEntry {
    private final Text LOCK_BUTTON_TOOLTIP = Text.translatable("language.default.tooltip");

    private final ButtonWidget lockButton = addChild(new TexturedButtonWidget(0, 0, 16, 24, 43, 0, 0,
            TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, __ -> {}));

    public LockedLanguageEntry(Runnable refreshListsAction, LanguageDefinition language, LinkedList<LanguageDefinition> selectedLanguages) {
        super(refreshListsAction, language, selectedLanguages);
        lockButton.active = false;
        lockButton.setTooltip(Tooltip.of(LOCK_BUTTON_TOOLTIP));
    }

    @Override
    protected void renderButtons(ButtonRenderer buttonRenderer, int top, int left) {
        buttonRenderer.render(lockButton, left + 6, top);
    }
}
