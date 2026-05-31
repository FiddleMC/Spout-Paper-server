package spout.clientview.model;

import com.mojang.serialization.Codec;
import spout.branding.SpoutNamespace;
import spout.server.paper.impl.packetmapping.item.reverse.ItemMappingReverser;
import spout.common.util.mojang.codec.EnumViaIdentifierCodec;
import org.jspecify.annotations.Nullable;
import java.util.List;

/**
 * The base implementation of {@link ClientView}.
 *
 * <p>
 * Every instance of {@link ClientView} is also an instance of {@link ClientViewImpl}.
 * </p>
 */
public abstract class ClientViewImpl implements ClientView {

    public static final Codec<AwarenessLevel> AWARENESS_LEVEL_CODEC = new EnumViaIdentifierCodec<>(ClientView.AwarenessLevel.class, SpoutNamespace.SPOUT);
    public static final Codec<List<AwarenessLevel>> AWARENESS_LEVEL_LIST_CODEC = Codec.list(AWARENESS_LEVEL_CODEC);

    /**
     * @return The {@link ItemMappingReverser} of this client,
     * or null if not available.
     *
     * <p>
     * The reverser (if present) instance stays the same during the entire connection session of a client.
     * </p>
     */
    public abstract @Nullable ItemMappingReverser getItemMappingReverser();

    public static ClientView getSimulatedForAwarenessLevel(AwarenessLevel awarenessLevel) {
        return switch (awarenessLevel) {
            case VANILLA -> SimulatedClientViewImpl.VANILLA_INSTANCE;
            case RESOURCE_PACK -> SimulatedClientViewImpl.RESOURCE_PACK_INSTANCE;
            case CLIENT_MOD -> SimulatedClientViewImpl.CLIENT_MOD_INSTANCE;
        };
    }

}
