package jerozgen.languagereload.gui;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.LinkedList;
import java.util.function.Consumer;

public class LockedLanguageEntry extends LanguageEntry {
    private static final Component LOCK_BUTTON_TOOLTIP = Component.translatable("language.default.tooltip");

    private final Button lockButton = addButton(new ImageButton(0, 0, 16, 24, 43, 0, 0,
            TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, __ -> {}, new Button.OnTooltip() {
        @Override
        public void onTooltip(Button button, PoseStack matrices, int mouseX, int mouseY) {
            if (client.screen != null) {
                client.screen.renderTooltip(matrices, LOCK_BUTTON_TOOLTIP, mouseX, mouseY);
            }
        }

        @Override
        public void narrateTooltip(Consumer<Component> consumer) {
            consumer.accept(LOCK_BUTTON_TOOLTIP);
        }
    }, CommonComponents.EMPTY));

    public LockedLanguageEntry(Runnable refreshListsAction, String code, LanguageInfo language, LinkedList<String> selectedLanguages) {
        super(refreshListsAction, code, language, selectedLanguages);
        lockButton.active = false;
    }

    @Override
    protected void renderButtons(ButtonRenderer renderer, int x, int y) {
        renderer.render(lockButton, x + 6, y);
    }
}