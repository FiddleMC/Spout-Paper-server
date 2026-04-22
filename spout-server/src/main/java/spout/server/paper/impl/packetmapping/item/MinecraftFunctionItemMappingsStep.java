package spout.server.paper.impl.packetmapping.item;

import java.util.function.Consumer;
import spout.server.paper.api.packetmapping.item.nms.ItemMappingHandleNMS;

/**
 * A {@link ItemMappingsStep} that performs a specific function.
 */
public record MinecraftFunctionItemMappingsStep(Consumer<ItemMappingHandleNMS> function) implements ItemMappingsStep {

    @Override
    public void apply(ItemMappingHandleNMSImpl handle) {
        this.function.accept(handle);
    }

}
