package spout.server.paper.api.moredatadriven.paper.registry.type.nms;

import net.minecraft.resources.Identifier;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeType;

/**
 * Extension of {@link ItemTypeType} using Minecraft internals.
 */
public interface ItemTypeTypeNMS extends ItemTypeType {

    @Override
    default NamespacedKey getNamespacedKey() {
        return CraftNamespacedKey.fromMinecraft(this.getIdentifier());
    }

    Identifier getIdentifier();

}
