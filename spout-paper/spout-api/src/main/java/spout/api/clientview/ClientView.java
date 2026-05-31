package spout.api.clientview;

import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import java.util.Arrays;

public interface ClientView {

    AwarenessLevel getAwarenessLevel();

    /**
     * @return The player of this client,
     * or null if not available.
     */
    @Nullable Player getPlayer();

    /**
     * @return The locale (lower-case, in the format that Minecraft uses,
     * such as "{@code ja_jp}" for Japanese) of this client,
     * or null if not available.
     */
    @Nullable String getLocale();

    /**
     * @return True only if this client understands all server-side translatables.
     * false if it can not be guaranteed.
     */
    boolean understandsAllServerSideTranslatables();

    /**
     * @return True only if this client understands all server-side items.
     * false if it can not be guaranteed.
     */
    boolean understandsAllServerSideItems();

    /**
     * @return True only if this client understands all server-side blocks.
     * false if it can not be guaranteed.
     */
    boolean understandsAllServerSideBlocks();

    /**
     * This enum represents the major categorization of the client's capability
     * to interpret data sent by the server.
     */
    enum AwarenessLevel {

        /**
         * For Java clients that have not accepted the server resource pack,
         * and also do not have the client mod.
         *
         * <p>
         * This generally results in data being replaced by the closest or most acceptable vanilla equivalent,
         * with additional rendering potentially being done through the use of vanilla entities.
         * </p>
         */
        VANILLA(false, false, false),

        /**
         * For Java clients that have accepted the server resource pack,
         * but do not have the client mod.
         *
         * <p>
         * This generally results in data being replaced by hosts that are overridden in the resource pack
         * (such as block states) or having additional data attached that links to the resource pack
         * (such as explicit item model).
         * Additional rendering can be done through the use of entities.
         * </p>
         */
        RESOURCE_PACK(true, false, false),

        /**
         * For Java clients that are have the client mod, i.e. they have the mod installed and are able to use
         * a sufficiently up-to-date version of it.
         *
         * <p>
         * This generally results in data being sent as-is, because when joining the server, the client receives
         * the necessary information to interpret the server-side block and item keys directly from then on.
         * </p>
         */
        CLIENT_MOD(true, true, true);

        private final boolean alwaysUnderstandsAllServerSideTranslatables;
        private final boolean alwaysUnderstandsAllServerSideBlocks;
        private final boolean alwaysUnderstandsAllServerSideItems;

        AwarenessLevel(
            boolean alwaysUnderstandsAllServerSideTranslatables,
            boolean alwaysUnderstandsAllServerSideBlocks,
            boolean alwaysUnderstandsAllServerSideItems
        ) {
            this.alwaysUnderstandsAllServerSideTranslatables = alwaysUnderstandsAllServerSideTranslatables;
            this.alwaysUnderstandsAllServerSideBlocks = alwaysUnderstandsAllServerSideBlocks;
            this.alwaysUnderstandsAllServerSideItems = alwaysUnderstandsAllServerSideItems;
        }

        public int getId() {
            return this.ordinal();
        }

        /**
         * @return True if every {@link ClientView} with this {@link AwarenessLevel}
         * will have {@link ClientView#understandsAllServerSideTranslatables} returning true.
         */
        public boolean alwaysUnderstandsAllServerSideTranslatables() {
            return this.alwaysUnderstandsAllServerSideTranslatables;
        }

        /**
         * @return True if every {@link ClientView} with this {@link AwarenessLevel}
         * will have {@link ClientView#understandsAllServerSideItems} returning true.
         */
        public boolean alwaysUnderstandsAllServerSideItems() {
            return this.alwaysUnderstandsAllServerSideItems;
        }

        /**
         * @return True if every {@link ClientView} with this {@link AwarenessLevel}
         * will have {@link ClientView#understandsAllServerSideBlocks} returning true.
         */
        public boolean alwaysUnderstandsAllServerSideBlocks() {
            return this.alwaysUnderstandsAllServerSideBlocks;
        }

        /**
         * Return value for {@link #getAll()},
         * or null if not initialized yet.
         */
        private static AwarenessLevel @Nullable [] all;

        /**
         * Convenience function that returns all {@link AwarenessLevel}s.
         *
         * @return An array of {@link AwarenessLevel}s.
         */
        public static AwarenessLevel[] getAll() {
            if (all == null) {
                all = values();
            }
            return all;
        }

        /**
         * Return value for {@link #getThatDoNotAlwaysUnderstandsAllServerSideTranslatables()},
         * or null if not initialized yet.
         */
        private static AwarenessLevel @Nullable [] thatDoNotAlwaysUnderstandsAllServerSideTranslatables;

        /**
         * Convenience function that returns all {@link AwarenessLevel}s
         * that have {@link #alwaysUnderstandsAllServerSideTranslatables()} returning false.
         *
         * @return An array of {@link AwarenessLevel}s.
         */
        public static AwarenessLevel[] getThatDoNotAlwaysUnderstandsAllServerSideTranslatables() {
            if (thatDoNotAlwaysUnderstandsAllServerSideTranslatables == null) {
                thatDoNotAlwaysUnderstandsAllServerSideTranslatables = Arrays.stream(getAll()).filter(level -> !level.alwaysUnderstandsAllServerSideTranslatables).toArray(AwarenessLevel[]::new);
            }
            return thatDoNotAlwaysUnderstandsAllServerSideTranslatables;
        }

        /**
         * Return value for {@link #getThatDoNotAlwaysUnderstandsAllServerSideBlocks()},
         * or null if not initialized yet.
         */
        private static AwarenessLevel @Nullable [] thatDoNotAlwaysUnderstandsAllServerSideBlocks;

        /**
         * Convenience function that returns all {@link AwarenessLevel}s
         * that have {@link #alwaysUnderstandsAllServerSideBlocks()} returning false.
         *
         * @return An array of {@link AwarenessLevel}s.
         */
        public static AwarenessLevel[] getThatDoNotAlwaysUnderstandsAllServerSideBlocks() {
            if (thatDoNotAlwaysUnderstandsAllServerSideBlocks == null) {
                thatDoNotAlwaysUnderstandsAllServerSideBlocks = Arrays.stream(getAll()).filter(level -> !level.alwaysUnderstandsAllServerSideBlocks).toArray(AwarenessLevel[]::new);
            }
            return thatDoNotAlwaysUnderstandsAllServerSideBlocks;
        }

        /**
         * Return value for {@link #getThatDoNotAlwaysUnderstandsAllServerSideItems()},
         * or null if not initialized yet.
         */
        private static AwarenessLevel @Nullable [] thatDoNotAlwaysUnderstandsAllServerSideItems;

        /**
         * Convenience function that returns all {@link AwarenessLevel}s
         * that have {@link #alwaysUnderstandsAllServerSideItems()} returning false.
         *
         * @return An array of {@link AwarenessLevel}s.
         */
        public static AwarenessLevel[] getThatDoNotAlwaysUnderstandsAllServerSideItems() {
            if (thatDoNotAlwaysUnderstandsAllServerSideItems == null) {
                thatDoNotAlwaysUnderstandsAllServerSideItems = Arrays.stream(getAll()).filter(level -> !level.alwaysUnderstandsAllServerSideItems).toArray(AwarenessLevel[]::new);
            }
            return thatDoNotAlwaysUnderstandsAllServerSideItems;
        }

    }

}
