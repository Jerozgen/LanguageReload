package jerozgen.languagereload.config;

import jerozgen.languagereload.LanguageReload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.Option;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends SimpleOptionsScreen {
    private static final CyclingOption<Boolean> MULTILINGUAL_SEARCH = CyclingOption.create(
            "options.languagereload.multilingualItemSearch",
            gameOptions -> Config.getInstance().multilingualItemSearch,
            (gameOptions, option, value) -> {
                Config.getInstance().multilingualItemSearch = value;
                Config.save();
                LanguageReload.reloadSearch();
            })
            .tooltip(client -> value -> client.textRenderer.wrapLines(new TranslatableText("options.languagereload.multilingualItemSearch.tooltip"), 200));

    public ConfigScreen(Screen parent) {
        super(
                parent,
                MinecraftClient.getInstance().options,
                new TranslatableText("options.languagereload.title"),
                new Option[]{MULTILINGUAL_SEARCH});
    }
}
