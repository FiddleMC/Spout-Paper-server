package spout.clientview.model;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import spout.api.clientview.ClientViewNMS;
import java.util.Locale;

/**
 * This class represents the static circumstances under which a client (typically a player) observes the data sent.
 *
 * <p>
 * An instance of this class represents a set of values that are static within a client's session with the server:
 * i.e. they stay the same for a client during the entire session.
 * </p>
 *
 * <p>
 * Note that for the purposes of this class, the session of the client only starts when it is able to have any
 * view of relevant data sent to it. For example, when a player joins, we first present them with the option
 * to accept or decline the resource pack. Until it has been confirmed that the resource pack has been definitively
 * loaded (so the moment after accepting, downloading and successful loading) or not loaded (so the moment it is either
 * confirmed that the player declined the resource pack, that the download failed or that another type of error
 * occurred after which we can be sure the resource pack will not be successfully loaded anymore), that player is not
 * assigned their {@link ClientView} yet. This also implies that we must make sure not to send any relevant
 * data (such as chunks, entities and potentially even the player's inventory - although that can be sent
 * according to a fallback view and updated as necessary) to the player before their view is determined.
 * </p>
 *
 * <p>
 * Apart from the interval before having definitely loaded or not loaded the resource pack, a player's session
 * (and as such their {@link ClientView} value) lasts from joining the server until disconnecting.
 * </p>
 *
 * <p>
 * This class may be extended to support additional values relevant to certain views
 * (such as the protocol version of the client).
 * </p>
 */
public interface ClientView extends ClientViewNMS {

    AwarenessLevel getAwarenessLevel();

    /**
     * @return The {@link Connection} of this client,
     * or null if not available.
     *
     * <p>
     * The connection (if present) instance stays the same during the entire connection session of a client.
     * </p>
     */
    @Nullable Connection getConnection();

    /**
     * @return The player of this client,
     * or null if not available (for example when the client is still in the configuration phase).
     */
    default @Nullable ServerPlayer getPlayerNMS() {
        @Nullable Connection connection = this.getConnection();
        return connection == null ? null : connection.getPlayer();
    }

    default @Nullable Player getPlayer() {
        @Nullable ServerPlayer playerNMS = this.getPlayerNMS();
        return playerNMS == null ? null : playerNMS.getBukkitEntity();
    }

    /**
     * @return The locale (lower-case, in the format that Minecraft uses,
     * such as "{@code ja_jp}" for Japanese) of this client,
     * or null if not available.
     */
    default @Nullable String getLocale() {
        @Nullable ServerPlayer player = this.getPlayerNMS();
        return player == null ? null : player.language.toLowerCase(Locale.ROOT);
    }

    /**
     * @return True only if this client understands all server-side translatables
     * (such as in the case of a client that has loaded the resource pack containing them),
     * false if it can not be guaranteed.
     */
    default boolean understandsAllServerSideTranslatables() {
        return this.getAwarenessLevel().alwaysUnderstandsAllServerSideTranslatables();
    }

    /**
     * @return True only if this client understands all server-side items
     * (such as in the case of a client mod that has added them to its client-side registry),
     * false if it can not be guaranteed.
     */
    default boolean understandsAllServerSideItems() {
        return this.getAwarenessLevel().alwaysUnderstandsAllServerSideItems();
    }

    /**
     * @return True only if this client understands all server-side blocks
     * (such as in the case of a client mod that has added them to its client-side registry),
     * false if it can not be guaranteed.
     */
    default boolean understandsAllServerSideBlocks() {
        return this.getAwarenessLevel().alwaysUnderstandsAllServerSideBlocks();
    }

}
