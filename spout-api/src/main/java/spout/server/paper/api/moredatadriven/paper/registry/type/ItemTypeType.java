package spout.server.paper.api.moredatadriven.paper.registry.type;

import org.bukkit.Keyed;

/**
 * An item type, implementing {@link Keyed}.
 */
public interface ItemTypeType extends Keyed {

    Object getWrappedCodec();

}
