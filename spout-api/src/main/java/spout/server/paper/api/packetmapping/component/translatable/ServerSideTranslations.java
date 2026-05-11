package spout.server.paper.api.packetmapping.component.translatable;

import spout.server.paper.api.util.composable.Composable;
import org.jspecify.annotations.Nullable;
import spout.server.paper.impl.util.service.SpoutServices;

/**
 * A service for registered server-side translations.
 *
 * <p>
 * This can also be used to override existing translations.
 * </p>
 */
public interface ServerSideTranslations extends Composable<ServerSideTranslationsComposeEvent> {

    /**
     * @return The {@link ServerSideTranslations} instance.
     */
    static ServerSideTranslations get() {
        return SpoutServices.getServerSideTranslations();
    }

    /**
     * An extent to which a registered translation can be used as a fallback.
     */
    enum FallbackScope {
        /**
         * The translation is only used for the specific locale.
         *
         * <p>
         * For example, when used with the locale {@code en_gb}, the translation with only apply to users who
         * have the locale {@code en_gb}.
         * </p>
         */
        NONE,
        /**
         * The translation is used for the specific locale,
         * but can also be used for other locales of the same language group if no specific
         * translation is available for that locale.
         *
         * <p>
         * For example, when used with the locale {@code en_gb}, the translation will apply to users who
         * have the locale {@code en_gb}, but also to any users who have a different {@code en_} locale,
         * such as {@code en_us} (unless a more specific translation has been registered).
         * </p>
         */
        LANGUAGE_GROUP,
        /**
         * The translation is used for the specific locale,
         * but can also be used for any other locale if no specific or {@link #LANGUAGE_GROUP} fallback
         * translation is available for that locale.
         *
         * <p>
         * For example, when used with the locale {@code en_gb}, the translation will apply to users who
         * have the locale {@code en_gb}, but also to any users who have another locale, such as {@code de_de}
         * (unless a more specific translation has been registered).
         * </p>
         */
        ALL
    }

    /**
     * A registered server-side translation.
     */
    record ServerSideTranslation(String translation, boolean overrideClientSide) {
    }

    /**
     * @return The best registered server-side translation for the given key and locale,
     * or null if none could be found.
     */
    @Nullable ServerSideTranslation get(String key, @Nullable String locale);

}
