package spout.server.paper.impl.moredatadriven.paper.registry.type;

import com.mojang.serialization.MapCodec;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.world.item.Item;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeType;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeTypeRegistryEntry;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.ItemTypeTypeRegistryEntryNMS;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.WrappedItemCodec;
import spout.server.paper.impl.moredatadriven.minecraft.type.WrappedItemCodecImpl;
import org.jspecify.annotations.Nullable;

/**
 * Implementation for {@link ItemTypeTypeRegistryEntry}.
 */
public class ItemTypeTypeRegistryEntryImpl implements ItemTypeTypeRegistryEntryNMS {

    protected @Nullable WrappedItemCodec<?> wrappedCodec;

    public ItemTypeTypeRegistryEntryImpl(Conversions conversions, @Nullable WrappedItemCodec<?> internal) {
        this.wrappedCodec = internal;
    }

    @Override
    public WrappedItemCodec<?> getWrappedCodec() {
        return this.wrappedCodec;
    }

    public static class BuilderImpl extends ItemTypeTypeRegistryEntryImpl implements ItemTypeTypeRegistryEntryNMS.Builder, PaperRegistryBuilder<WrappedItemCodec<?>, ItemTypeType> {

        public BuilderImpl(Conversions conversions, @Nullable WrappedItemCodec<?> internal) {
            super(conversions, internal);
        }

        @Override
        public void setCodec(MapCodec<? extends Item> codecForType) {
            this.wrappedCodec = WrappedItemCodecImpl.wrap(codecForType);
        }

        @Override
        public WrappedItemCodec<?> build() {
            return this.wrappedCodec;
        }

    }

}
