package spout.server.paper.impl.clientview;

import net.minecraft.network.Connection;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.impl.packetmapping.item.reverse.ItemMappingReverser;
import org.jspecify.annotations.Nullable;
import java.lang.ref.WeakReference;

/**
 * An abstract implementation of {@link ClientView}
 * for clients that represent a {@link Connection}.
 */
public abstract class ConnectionClientViewImpl extends ClientViewImpl {

    /**
     * The {@link Connection} tied to this client.
     */
    private final WeakReference<Connection> connection;

    protected ConnectionClientViewImpl(Connection connection) {
        this.connection = new WeakReference<>(connection);
    }

    @Override
    public @Nullable Connection getConnection() {
        return this.connection.get();
    }

    @Override
    public @Nullable ItemMappingReverser getItemMappingReverser() {
        return this.getConnection().itemMappingReverser;
    }

}
