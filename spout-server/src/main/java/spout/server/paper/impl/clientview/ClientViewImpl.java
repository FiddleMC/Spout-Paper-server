package spout.server.paper.impl.clientview;

import com.mojang.serialization.Codec;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.clientview.nms.NMSClientView;
import spout.common.branding.SpoutNamespace;
import spout.server.paper.impl.packetmapping.item.reverse.ItemMappingReverser;
import spout.server.paper.impl.util.mojang.codec.EnumViaIdentifierCodec;
import org.jspecify.annotations.Nullable;
import java.util.List;

/**
 * The base implementation of {@link ClientView}.
 *
 * <p>
 * Every instance of {@link ClientView} is also an instance of {@link ClientViewImpl}.
 * </p>
 */
public abstract class ClientViewImpl implements NMSClientView {

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

}
