package org.fiddlemc.fiddle.impl.packetmapping.component.translatable;

import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslations;
import org.fiddlemc.fiddle.api.packetmapping.component.translatable.ServerSideTranslationsComposeEvent;
import org.fiddlemc.fiddle.impl.util.composable.ComposableImpl;
import org.fiddlemc.fiddle.impl.util.java.serviceloader.NoArgsConstructorServiceProviderImpl;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

}
