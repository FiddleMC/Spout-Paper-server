package spout.server.paper.api.moredatadriven.paper.registry.type.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.Item;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeType;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeTypeRegistryEntry;

/**
 * An extension of {@link ItemTypeTypeRegistryEntry} using Minecraft internals.
 */
public interface ItemTypeTypeRegistryEntryNMS extends ItemTypeTypeRegistryEntry {

    @Override
    WrappedItemCodec<?> getWrappedCodec();

    @Override
    default MapCodec<? extends Item> getCodec() {
        return this.getWrappedCodec().getCodec();
    }

    /**
     * A {@link ItemTypeTypeRegistryEntry.Builder}
     * that allows building an {@link ItemTypeType} type using Minecraft internals.
     */
    interface Builder extends ItemTypeTypeRegistryEntryNMS, ItemTypeTypeRegistryEntry.Builder {

        void setCodec(MapCodec<? extends Item> codecForType);

    }

}
