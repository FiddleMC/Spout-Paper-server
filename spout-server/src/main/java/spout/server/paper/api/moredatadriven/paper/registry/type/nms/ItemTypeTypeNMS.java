package spout.server.paper.api.moredatadriven.paper.registry.type.nms;

import net.minecraft.world.item.Item;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeType;

/**
 * Extension of {@link ItemTypeType} using Minecraft internals.
 */
public interface ItemTypeTypeNMS<I extends Item> extends ItemTypeType {

    @Override
    WrappedItemCodec<I> getWrappedCodec();

}
