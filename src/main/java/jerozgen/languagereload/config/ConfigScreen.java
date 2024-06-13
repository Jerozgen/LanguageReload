package jerozgen.languagereload.config;

import jerozgen.languagereload.LanguageReload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends GameOptionsScreen {
    private static final SimpleOption<Boolean> MULTILINGUAL_SEARCH = SimpleOption.ofBoolean(
            "options.languagereload.multilingualItemSearch",
            SimpleOption.constantTooltip(Text.translatable("options.languagereload.multilingualItemSearch.tooltip")),
            true,
            value -> {
                Config.getInstance().multilingualItemSearch = value;
                Config.save();
                LanguageReload.reloadLanguages();
            });

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("options.languagereload.title"));
        MULTILINGUAL_SEARCH.setValue(Config.getInstance().multilingualItemSearch);
    }

    @Override
    protected void addOptions() {
        if (body != null) {
            body.addAll(MULTILINGUAL_SEARCH);
        }
    }
}
