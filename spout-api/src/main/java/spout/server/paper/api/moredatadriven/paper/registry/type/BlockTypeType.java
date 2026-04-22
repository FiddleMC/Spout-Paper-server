package spout.server.paper.api.moredatadriven.paper.registry.type;

import org.bukkit.Keyed;

/**
 * A block type, implementing {@link Keyed}.
 */
public interface BlockTypeType extends Keyed {

    Object getWrappedCodec();

}
