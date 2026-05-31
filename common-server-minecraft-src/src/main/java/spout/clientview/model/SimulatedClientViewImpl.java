package spout.clientview.model;

import net.minecraft.network.Connection;
import org.jspecify.annotations.Nullable;
import spout.server.paper.impl.packetmapping.item.reverse.ItemMappingReverser;

/**
 * A fake {@link ClientView} that is used to simulate mappings
 * outside a context for a specific client.
 */
public final class SimulatedClientViewImpl extends ClientViewImpl {

    private final AwarenessLevel awarenessLevel;

    public static final SimulatedClientViewImpl VANILLA_INSTANCE = new SimulatedClientViewImpl(AwarenessLevel.VANILLA);
    public static final SimulatedClientViewImpl RESOURCE_PACK_INSTANCE = new SimulatedClientViewImpl(AwarenessLevel.RESOURCE_PACK);
    public static final SimulatedClientViewImpl CLIENT_MOD_INSTANCE = new SimulatedClientViewImpl(AwarenessLevel.CLIENT_MOD);

    private SimulatedClientViewImpl(AwarenessLevel awarenessLevel) {
        super();
        this.awarenessLevel = awarenessLevel;
    }

    @Override
    public AwarenessLevel getAwarenessLevel() {
        return this.awarenessLevel;
    }

    @Override
    public @Nullable Connection getConnection() {
        return null;
    }

    @Override
    public @Nullable ItemMappingReverser getItemMappingReverser() {
        return null;
    }

}
