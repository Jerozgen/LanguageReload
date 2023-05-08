package jerozgen.languagereload.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public abstract class LanguageEntry extends ObjectSelectionList.Entry<LanguageEntry> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(LanguageReload.MOD_ID, "textures/gui/language_selection.png");
    protected static final int TEXTURE_WIDTH = 64;
    protected static final int TEXTURE_HEIGHT = 64;
    protected static final int HOVERED_V_OFFSET = 24;

    protected final Minecraft client = Minecraft.getInstance();

    protected final String code;
    protected final LanguageInfo language;
    protected final LinkedList<String> selectedLanguages;
    protected final Runnable refreshListsAction;

    private final List<AbstractWidget> buttons = new ArrayList<>();

    protected LanguageListWidget parentList;

    public LanguageEntry(Runnable refreshListsAction, String code, LanguageInfo language, LinkedList<String> selectedLanguages) {
        this.code = code;
        this.language = language;
        this.selectedLanguages = selectedLanguages;
        this.refreshListsAction = refreshListsAction;
    }

    protected Button addButton(int width, int height, int u, int v, Button.OnPress action) {
        return addButton(new ImageButton(0, 0, width, height, u, v, HOVERED_V_OFFSET, TEXTURE, TEXTURE_WIDTH, TEXTURE_HEIGHT, action));
    }

    protected Button addButton(ImageButton button) {
        button.visible = false;
        buttons.add(button);
        return button;
    }

    public boolean isFocused() {
        return parentList.getSelected() == this && parentList.isFocused();
    }

    public void toggle() {}

    public void moveUp() {}

    public void moveDown() {}

    protected abstract void renderButtons(ButtonRenderer buttonRenderer, int x, int y);

    @Override
    public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        x -= 2;
        y -= 2;
        if (hovered || isFocused() || client.options.touchscreen().get()) {
            GuiComponent.fill(matrices,
                    x + 1, y + 1, x + entryWidth - 1, y + entryHeight + 3,
                    (hovered || isFocused()) ? 0xA0909090 : 0x50909090);
            buttons.forEach(button -> button.visible = false);
            renderButtons((button, buttonX, buttonY) -> {
                button.x = buttonX;
                button.y = buttonY;
                button.visible = true;
                button.render(matrices, mouseX, mouseY, tickDelta);
            }, x, y);
        }
        client.font.drawShadow(matrices, language.getName(), x + 29, y + 3, 0xFFFFFF);
        client.font.drawShadow(matrices, language.getRegion(), x + 29, y + 14, 0x808080);
    }

    @Override
    public Component getNarration() {
        return Component.translatable("narrator.select", language);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var widget : buttons)
            if (widget.mouseClicked(mouseX, mouseY, button)) {
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

    public LanguageInfo getLanguage() {
        return language;
    }

    @FunctionalInterface
    protected interface ButtonRenderer {
        void render(Button button, int x, int y);
    }
}