package jerozgen.languagereload.config;

import jerozgen.languagereload.LanguageReload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends OptionsSubScreen {
    private static final OptionInstance<Boolean> MULTILINGUAL_SEARCH = OptionInstance.createBoolean(
            "options.languagereload.multilingualItemSearch",
            OptionInstance.cachedConstantTooltip(
                    Component.translatable("options.languagereload.multilingualItemSearch.tooltip")),
            true,
            value -> {
                Config.getInstance().multilingualItemSearch = value;
                Config.save();
                LanguageReload.reloadLanguages();
            });

    private static final OptionInstance<Boolean> REMOVABLE_DEFAULT_LANGUAGE = OptionInstance.createBoolean(
            "options.languagereload.removableDefaultLanguage",
            (value) -> value
                    ? Tooltip.create(
                            Component.translatable("options.languagereload.removableDefaultLanguage.removable.tooltip"))
                    : Tooltip.create(
                            Component.translatable("options.languagereload.removableDefaultLanguage.fixed.tooltip")),
            (__, value) -> value
                    ? Component.translatable("options.languagereload.removableDefaultLanguage.removable")
                    : Component.translatable("options.languagereload.removableDefaultLanguage.fixed"),
            false,
            value -> {
                Config.getInstance().removableDefaultLanguage = value;
                Config.save();
            });

    public ConfigScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("options.languagereload.title"));
        MULTILINGUAL_SEARCH.set(Config.getInstance().multilingualItemSearch);
        REMOVABLE_DEFAULT_LANGUAGE.set(Config.getInstance().removableDefaultLanguage);
    }

    @Override
    protected void addOptions() {
        if (list != null) {
            list.addSmall(MULTILINGUAL_SEARCH, REMOVABLE_DEFAULT_LANGUAGE);
        }
    }
}
