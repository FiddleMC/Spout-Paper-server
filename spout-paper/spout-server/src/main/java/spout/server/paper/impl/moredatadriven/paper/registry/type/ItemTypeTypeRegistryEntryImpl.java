package spout.server.paper.impl.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.util.Conversions;
import spout.common.moredatadriven.minecraft.itemtype.SpoutItemType;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeType;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeTypeRegistryEntry;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.ItemTypeTypeRegistryEntryNMS;
import org.jspecify.annotations.Nullable;

/**
 * Implementation for {@link ItemTypeTypeRegistryEntry}.
 */
public class ItemTypeTypeRegistryEntryImpl implements ItemTypeTypeRegistryEntryNMS {

    protected @Nullable SpoutItemType itemType;

    public ItemTypeTypeRegistryEntryImpl(Conversions conversions, @Nullable SpoutItemType internal) {
        this.itemType = internal;
    }

    public static class BuilderImpl extends ItemTypeTypeRegistryEntryImpl implements ItemTypeTypeRegistryEntryNMS.Builder, PaperRegistryBuilder<SpoutItemType, ItemTypeType> {

        public BuilderImpl(Conversions conversions, @Nullable SpoutItemType internal) {
            super(conversions, internal);
        }

        // @Override
        // public void setCodec(MapCodec<? extends Item> codecForType) {
        //     this.itemType = WrappedItemCodecImpl.wrap(codecForType);
        // }

        @Override
        public SpoutItemType build() {
            return this.itemType;
        }

    }

}
