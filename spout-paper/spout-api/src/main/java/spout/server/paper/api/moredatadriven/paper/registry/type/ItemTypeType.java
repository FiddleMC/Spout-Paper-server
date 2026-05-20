package spout.server.paper.api.moredatadriven.paper.registry.type;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

/**
 * An item type, implementing {@link Keyed}.
 */
public interface ItemTypeType extends Keyed {

    NamespacedKey getNamespacedKey();

}
