package spout.server.paper.api.resourcepack.construct;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEvent;
import spout.server.paper.api.clientview.ClientView;

/**
 * Called when the Spout server resource pack has been constructed.
 */
public interface ResourcePackConstructFinishEvent extends LifecycleEvent {

    /**
     * @param awarenessLevel A {@link ClientView.AwarenessLevel}.
     * @return The constructed resource pack for the given awareness level.
     * @throws IllegalArgumentException If the given {@link ClientView.AwarenessLevel}
     *                                  does not support a resource pack.
     */
    ConstructedResourcePack get(ClientView.AwarenessLevel awarenessLevel);

}
