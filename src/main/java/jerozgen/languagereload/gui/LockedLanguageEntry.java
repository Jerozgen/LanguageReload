package jerozgen.languagereload.gui;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;
import org.joml.Vector2i;

import java.util.LinkedList;

public class LockedLanguageEntry extends LanguageEntry {
    private static final Text LOCK_BUTTON_TOOLTIP = Text.translatable("language.default.tooltip");

    private final ButtonWidget lockButton = addButton(16, 24, 43, 0, 0, __ -> {});

    public LockedLanguageEntry(Runnable refreshListsAction, String code, LanguageDefinition language, LinkedList<String> selectedLanguages) {
        super(refreshListsAction, code, language, selectedLanguages);
        lockButton.active = false;
        lockButton.setTooltip(Tooltip.of(LOCK_BUTTON_TOOLTIP));
    }

    @Override
    protected void renderButtons(ButtonRenderer renderer, int x, int y) {
        renderer.render(lockButton, x + 6, y);
        if (this.isFocused()) {
            var tooltip = client.textRenderer.wrapLines(LOCK_BUTTON_TOOLTIP, parentList.getRowWidth() - 6);
            parentList.getScreen().setTooltip(tooltip, (screen, mouseX, mouseY, width, height) -> {
                var pos = new Vector2i(x + 3, y + parentList.getRowHeight() + 4);
                if (pos.y > parentList.getBottom() + 2 || pos.y + height + 5 > screen.height) {
                    pos.y = y - height - 6;
                }
                return pos;
            }, true);
        }
    }
}
