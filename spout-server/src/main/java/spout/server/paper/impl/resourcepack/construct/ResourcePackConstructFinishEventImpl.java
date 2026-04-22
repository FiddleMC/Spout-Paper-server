package spout.server.paper.impl.resourcepack.construct;

import io.papermc.paper.plugin.lifecycle.event.PaperLifecycleEvent;
import spout.server.paper.api.clientview.ClientView;
import spout.server.paper.api.resourcepack.construct.ConstructedResourcePack;
import spout.server.paper.api.resourcepack.construct.ResourcePackConstructFinishEvent;
import org.jspecify.annotations.Nullable;
import java.util.Map;

/**
 * The implementation for {@link ResourcePackConstructFinishEvent}.
 */
public record ResourcePackConstructFinishEventImpl(Map<ClientView.AwarenessLevel, ConstructedResourcePackImpl> packs) implements ResourcePackConstructFinishEvent, PaperLifecycleEvent {

    @Override
    public ConstructedResourcePack get(ClientView.AwarenessLevel awarenessLevel) {
        @Nullable ConstructedResourcePack pack = this.packs.get(awarenessLevel);
        if (pack == null) {
            throw new IllegalArgumentException("No generated resource pack exists for awareness level " + awarenessLevel);
        }
        return pack;
    }

}
