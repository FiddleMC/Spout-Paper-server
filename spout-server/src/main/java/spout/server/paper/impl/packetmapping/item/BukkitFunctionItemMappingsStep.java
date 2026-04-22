package spout.server.paper.impl.packetmapping.item;

import java.util.function.Consumer;
import spout.server.paper.api.packetmapping.item.ItemMappingHandle;

/**
 * A {@link ItemMappingsStep} that performs a specific function.
 */
public record BukkitFunctionItemMappingsStep(Consumer<ItemMappingHandle> function) implements ItemMappingsStep {

    @Override
    public void apply(ItemMappingHandleNMSImpl handle) {
        this.function.accept(handle.bukkitHandle());
    }

}
