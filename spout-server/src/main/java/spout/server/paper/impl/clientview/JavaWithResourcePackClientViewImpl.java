package spout.server.paper.impl.clientview;

import net.minecraft.network.Connection;
import spout.server.paper.api.clientview.ClientView;

/**
 * A simple implementation of {@link ClientView}
 * for {@link AwarenessLevel#RESOURCE_PACK} clients.
 */
public class JavaWithResourcePackClientViewImpl extends ConnectionClientViewImpl {

    public JavaWithResourcePackClientViewImpl(Connection connection) {
        super(connection);
    }

    @Override
    public AwarenessLevel getAwarenessLevel() {
        return AwarenessLevel.RESOURCE_PACK;
    }

}
