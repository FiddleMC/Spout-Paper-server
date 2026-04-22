package spout.server.paper.impl.moredatadriven.paper.registry.type;

import com.mojang.serialization.MapCodec;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.data.util.Conversions;
import net.minecraft.world.level.block.Block;
import spout.server.paper.api.moredatadriven.paper.registry.type.BlockTypeType;
import spout.server.paper.api.moredatadriven.paper.registry.type.BlockTypeTypeRegistryEntry;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.BlockTypeTypeRegistryEntryNMS;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.WrappedBlockCodec;
import spout.server.paper.impl.moredatadriven.minecraft.type.WrappedBlockCodecImpl;
import org.jspecify.annotations.Nullable;

/**
 * Implementation for {@link BlockTypeTypeRegistryEntry}.
 */
public class BlockTypeTypeRegistryEntryImpl implements BlockTypeTypeRegistryEntryNMS {

    protected @Nullable WrappedBlockCodec<?> wrappedCodec;

    public BlockTypeTypeRegistryEntryImpl(Conversions conversions, @Nullable WrappedBlockCodec<?> internal) {
        this.wrappedCodec = internal;
    }

    @Override
    public WrappedBlockCodec<?> getWrappedCodec() {
        return this.wrappedCodec;
    }

    public static class BuilderImpl extends BlockTypeTypeRegistryEntryImpl implements BlockTypeTypeRegistryEntryNMS.Builder, PaperRegistryBuilder<WrappedBlockCodec<?>, BlockTypeType> {

        public BuilderImpl(Conversions conversions, @Nullable WrappedBlockCodec<?> internal) {
            super(conversions, internal);
        }

        @Override
        public void setCodec(MapCodec<? extends Block> codecForType) {
            this.wrappedCodec = WrappedBlockCodecImpl.wrap(codecForType);
        }

        @Override
        public WrappedBlockCodec<?> build() {
            return this.wrappedCodec;
        }

    }

}
