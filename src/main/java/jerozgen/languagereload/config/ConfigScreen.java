package jerozgen.languagereload.config;

import jerozgen.languagereload.LanguageReload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
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

    private static final SimpleOption<Boolean> REMOVABLE_DEFAULT_LANGUAGE = SimpleOption.ofBoolean(
            "options.languagereload.removableDefaultLanguage",
            (value) -> value
                    ? Tooltip.of(Text.translatable("options.languagereload.removableDefaultLanguage.removable.tooltip"))
                    : Tooltip.of(Text.translatable("options.languagereload.removableDefaultLanguage.fixed.tooltip")),
            (__, value) -> value
                    ? Text.translatable("options.languagereload.removableDefaultLanguage.removable")
                    : Text.translatable("options.languagereload.removableDefaultLanguage.fixed"),
            false,
            value -> {
                Config.getInstance().removableDefaultLanguage = value;
                Config.save();
            }
    );

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("options.languagereload.title"));
        MULTILINGUAL_SEARCH.setValue(Config.getInstance().multilingualItemSearch);
        REMOVABLE_DEFAULT_LANGUAGE.setValue(Config.getInstance().removableDefaultLanguage);
    }

    @Override
    protected void addOptions() {
        if (body != null) {
            body.addAll(MULTILINGUAL_SEARCH);
            body.addAll(REMOVABLE_DEFAULT_LANGUAGE);
        }
    }
}
