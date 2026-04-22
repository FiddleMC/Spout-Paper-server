package spout.server.paper.impl.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import spout.server.paper.api.moredatadriven.paper.registry.type.BlockTypeType;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.BlockTypeTypeNMS;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.WrappedBlockCodec;

/**
 * The implementation for {@link BlockTypeType}.
 */
public final class BlockTypeTypeImpl<B extends Block> extends HolderableBase<WrappedBlockCodec<B>> implements BlockTypeTypeNMS<B> {

    public BlockTypeTypeImpl(Holder<WrappedBlockCodec<B>> wrappedCodecHolder) {
        super(wrappedCodecHolder);
    }

    @Override
    public WrappedBlockCodec<B> getWrappedCodec() {
        return this.getHandle();
    }

    public static <B extends Block> BlockTypeTypeImpl<B> of(Holder<WrappedBlockCodec<?>> wrappedCodecHolder) {
        return new BlockTypeTypeImpl<>((Holder<WrappedBlockCodec<B>>) (Holder) wrappedCodecHolder);
    }

}
