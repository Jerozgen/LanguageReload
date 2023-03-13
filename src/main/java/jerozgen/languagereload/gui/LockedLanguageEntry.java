package jerozgen.languagereload.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.function.Consumer;

public class LockedLanguageEntry extends LanguageEntry {
    private static final Text LOCK_BUTTON_TOOLTIP = Text.translatable("language.default.tooltip");

    private final ButtonWidget lockButton = addButton(new TexturedButtonWidget(0, 0, 16, 24, 43, 0, 0,
            TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, __ -> {}, new ButtonWidget.TooltipSupplier() {
        @Override
        public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
            if (client.currentScreen != null) {
                client.currentScreen.renderTooltip(matrices, LOCK_BUTTON_TOOLTIP, mouseX, mouseY);
            }
        }

        @Override
        public void supply(Consumer<Text> consumer) {
            consumer.accept(LOCK_BUTTON_TOOLTIP);
        }
    }, ScreenTexts.EMPTY));

    public LockedLanguageEntry(Runnable refreshListsAction, String code, LanguageDefinition language, LinkedList<String> selectedLanguages) {
        super(refreshListsAction, code, language, selectedLanguages);
        lockButton.active = false;
    }

    @Override
    protected void renderButtons(ButtonRenderer renderer, int x, int y) {
        renderer.render(lockButton, x + 6, y);
    }
}
