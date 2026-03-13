package org.fiddlemc.fiddle.impl.packetmapping.component.translatable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslationsComposeEvent;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The implementation of {@link ServerSideTranslations}.
 */
public final class ServerSideTranslationsImpl extends ComposableImpl<ServerSideTranslationsComposeEvent, ServerSideTranslationsComposeEventImpl> implements ServerSideTranslations {

    public static final class ServiceProviderImpl extends NoArgsConstructorServiceProviderImpl<ServerSideTranslations, ServerSideTranslationsImpl> implements ServiceProvider {

        public ServiceProviderImpl() {
            super(ServerSideTranslationsImpl.class);
        }

    }

    public static ServerSideTranslationsImpl get() {
        return (ServerSideTranslationsImpl) ServerSideTranslations.get();
    }

    @Override
    protected String getEventTypeNamePrefix() {
        return "fiddle_server_side_translation_registrar";
    }

    @Override
    protected ServerSideTranslationsComposeEventImpl createComposeEvent() {
        return new ServerSideTranslationsComposeEventImpl(this);
    }

    /**
     * A map of the registered translations per key.
     */
    final Map<String, RegisteredTranslationsForKey> registeredTranslations = new HashMap<>();

    static final class RegisteredTranslationsForKey {

        /**
         * A translation that can act as the fallback for any locale,
         * or null if none is registered.
         */
        @Nullable ServerSideTranslation genericTranslation;

        /**
         * Translations that can act as the fallback for specific language groups,
         * indexed by their lower-case language group,
         * or null if none are registered.
         */
        @Nullable Map<String, ServerSideTranslation> languageGroupTranslations;

        /**
         * Translations for specific locales,
         * indexed by their lower-case locale.
         */
        Map<String, ServerSideTranslation> localeTranslations = new HashMap<>(2);

    }

    /**
     * @return Whether any translations are registered for the given key.
     */
    public boolean hasAny(String key) {
        String lowerCaseKey = key.toLowerCase(Locale.ROOT);
        RegisteredTranslationsForKey registeredTranslationsForKey = this.registeredTranslations.get(lowerCaseKey);
        if (registeredTranslationsForKey != null) {
            if (registeredTranslationsForKey.genericTranslation != null || (registeredTranslationsForKey.languageGroupTranslations != null && !registeredTranslationsForKey.languageGroupTranslations.isEmpty()) || !registeredTranslationsForKey.localeTranslations.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable ServerSideTranslation get(String key, @Nullable String locale) {
        String lowerCaseKey = key.toLowerCase(Locale.ROOT);
        @Nullable String lowerCaseLocale = locale == null ? null : locale.toLowerCase(Locale.ROOT);
        @Nullable RegisteredTranslationsForKey translationsForKey = registeredTranslations.get(lowerCaseKey);
        if (translationsForKey == null) {
            return null;
        }
        @Nullable ServerSideTranslation translation = null;
        if (lowerCaseLocale != null) {
            translation = translationsForKey.localeTranslations == null ? null : translationsForKey.localeTranslations.get(lowerCaseLocale);
            if (translation != null && translation.overrideClientSide()) {
                return translation;
            }
            @Nullable String group = MinecraftLocaleUtil.getLanguageGroup(lowerCaseLocale);
            if (group != null) {
                @Nullable ServerSideTranslation alternative = translationsForKey.languageGroupTranslations == null ? null : translationsForKey.languageGroupTranslations.get(group);
                if (alternative != null) {
                    if (alternative.overrideClientSide()) {
                        return alternative;
                    }
                    if (translation == null) {
                        translation = alternative;
                    }
                }
            }
        }
        @Nullable ServerSideTranslation alternative = translationsForKey.genericTranslation;
        if (alternative != null) {
            if (alternative.overrideClientSide()) {
                return alternative;
            }
            if (translation == null) {
                translation = alternative;
            }
        }
        return translation;
    }

    private static @Nullable ServerSideTranslation keepIfAllowed(@Nullable ServerSideTranslation translation, boolean keyIsVanilla) {
        return translation != null && (!keyIsVanilla || translation.overrideClientSide()) ? translation : null;
    }

    private Map<String, Map<String, String>> exportForResourcePackAsMaps() {
        MinecraftLocaleUtil.KnownLocale defaultLocale = MinecraftLocaleUtil.getDefault();
        Set<String> vanillaKeys;
        try {
            vanillaKeys = JsonParser.parseString(new String(this.getClass().getClassLoader().getResourceAsStream("assets/minecraft/lang/" + defaultLocale.lowerCaseLocale + ".json").readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject().keySet();
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while reading vanilla default language file");
        }
        @Nullable Map<String, Map<String, String>> exported = new HashMap<>();
        for (Map.Entry<String, RegisteredTranslationsForKey> registeredTranslationEntry : this.registeredTranslations.entrySet()) {

            // Unpack the entry
            String key = registeredTranslationEntry.getKey();
            RegisteredTranslationsForKey translations = registeredTranslationEntry.getValue();
            boolean keyIsVanilla = vanillaKeys.contains(key);

            // Determine the translation for the default locale
            @Nullable ServerSideTranslation defaultLocaleTranslation = keepIfAllowed(translations.localeTranslations.get(defaultLocale.lowerCaseLocale), keyIsVanilla);
            if (defaultLocaleTranslation == null) {
                if (defaultLocale.languageGroup != null) {
                    defaultLocaleTranslation = keepIfAllowed(translations.languageGroupTranslations.get(defaultLocale.languageGroup), keyIsVanilla);
                }
                if (defaultLocaleTranslation == null) {
                    defaultLocaleTranslation = keepIfAllowed(translations.genericTranslation, keyIsVanilla);
                }
            }

            // Fill in the translations for specific locales
            for (Map.Entry<String, ServerSideTranslation> localeTranslationEntry : translations.localeTranslations.entrySet()) {

                // Unpack the entry
                String locale = localeTranslationEntry.getKey();
                @Nullable ServerSideTranslation translation = keepIfAllowed(localeTranslationEntry.getValue(), keyIsVanilla);
                if (translation == null) continue;

                // Store in the map
                exported.computeIfAbsent(locale, $ -> new HashMap<>()).put(key, translation.translation());

            }

            // Fill in the translations for language groups, where not filled in yet
            if (translations.languageGroupTranslations != null) {
                for (Map.Entry<String, ServerSideTranslation> languageGroupTranslationEntry : translations.languageGroupTranslations.entrySet()) {

                    // Unpack the entry
                    String languageGroup = languageGroupTranslationEntry.getKey();
                    @Nullable ServerSideTranslation translation = keepIfAllowed(languageGroupTranslationEntry.getValue(), keyIsVanilla);
                    if (translation == null) continue;

                    // Store in the map
                    for (MinecraftLocaleUtil.KnownLocale locale : MinecraftLocaleUtil.getKnownLocalesForLanguageGroup(languageGroup)) {
                        exported.computeIfAbsent(locale.lowerCaseLocale, $ -> new HashMap<>()).putIfAbsent(key, translation.translation());
                    }

                }

                // Fill in the generic translation, where not filled in yet
                @Nullable ServerSideTranslation genericTranslation = keepIfAllowed(translations.genericTranslation, keyIsVanilla);
                if (genericTranslation != null) {
                    // Only if the translation is different from the default one
                    if (defaultLocaleTranslation == null || !genericTranslation.translation().equals(defaultLocaleTranslation.translation())) {
                        // Store in the map
                        for (MinecraftLocaleUtil.KnownLocale locale : MinecraftLocaleUtil.getKnownLocales()) {
                            exported.computeIfAbsent(locale.lowerCaseLocale, $ -> new HashMap<>()).putIfAbsent(key, genericTranslation.translation());
                        }
                    }
                }

                // Remove any translations equal to the default
                if (defaultLocaleTranslation != null) {
                    for (Map.Entry<String, Map<String, String>> exportedEntry : exported.entrySet()) {
                        String translation = exportedEntry.getValue().get(key);
                        if (translation != null && translation.equals(defaultLocaleTranslation.translation())) {
                            exportedEntry.getValue().remove(key);
                        }
                    }
                    // Add the default back
                    exported.computeIfAbsent(defaultLocale.lowerCaseLocale, $ -> new HashMap<>()).put(key, defaultLocaleTranslation.translation());
                }

            }

        }
        exported = exported.entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return exported;
    }

    public Map<String, JsonObject> exportForResourcePackAsJSONs() {
        return this.exportForResourcePackAsMaps().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            JsonObject json = new JsonObject();
            for (Map.Entry<String, String> mapsEntry : entry.getValue().entrySet()) {
                json.addProperty(mapsEntry.getKey(), mapsEntry.getValue());
            }
            return json;
        }));
    }

}
