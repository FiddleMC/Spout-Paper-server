package spout.api.clientview;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

/**
 * An extension of the {@link ClientView} interface that adds methods to get Minecraft internals.
 *
 * <p>
 * Every instance of {@link ClientView} is also an instance of {@link ClientViewNMS}.
 * </p>
 */
public interface ClientViewNMS extends ClientView {

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
     * @return The {@link ServerPlayer} of this client,
     * or null if not available (for example when the client is still in the configuration phase).
     */
    @Nullable ServerPlayer getPlayerNMS();

}
