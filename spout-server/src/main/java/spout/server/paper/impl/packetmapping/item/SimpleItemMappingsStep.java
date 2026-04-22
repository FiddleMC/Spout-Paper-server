package spout.server.paper.impl.packetmapping.item;

import net.minecraft.world.item.Item;
import spout.server.paper.api.packetmapping.item.nms.ItemMappingUtilitiesNMS;
import spout.server.paper.impl.packetmapping.block.BlockMappingsStep;

/**
 * A {@link BlockMappingsStep} that always maps to a specific {@link Item}
 * while {@linkplain ItemMappingUtilitiesNMS#setItemWhilePreservingRest} preserving other properties}.
 */
public record SimpleItemMappingsStep(Item to) implements ItemMappingsStep {

    @Override
    public void apply(ItemMappingHandleNMSImpl handle) {
        ItemMappingUtilitiesNMS.get().setItemWhilePreservingRest(handle, to);
    }

}
