package spout.clientview.model;

import net.minecraft.network.Connection;

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
