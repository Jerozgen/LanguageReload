package jerozgen.languagereload.gui;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class LanguageEntry extends AlwaysSelectedEntryListWidget.Entry<LanguageEntry> {
    protected final MinecraftClient client = MinecraftClient.getInstance();

    protected final String code;
    protected final LanguageDefinition language;
    protected final LinkedList<String> selectedLanguages;
    protected final Runnable refreshListsAction;

    private final List<ClickableWidget> buttons = new ArrayList<>();

    protected LanguageListWidget parentList;

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

    public void toggle() {}

    public void moveUp() {}

    public void moveDown() {}

    protected abstract void renderButtons(ButtonRenderer buttonRenderer, int x, int y);

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
        }
        context.drawTextWithShadow(client.textRenderer, language.name(), x + 29, y + 3, 0xFFFFFF);
        context.drawTextWithShadow(client.textRenderer, language.region(), x + 29, y + 14, 0x808080);
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
    protected interface ButtonRenderer {
        void render(ButtonWidget button, int x, int y);
    }
}
