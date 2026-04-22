package spout.server.paper.impl.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeType;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.ItemTypeTypeNMS;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.WrappedItemCodec;

/**
 * The implementation for {@link ItemTypeType}.
 */
public final class ItemTypeTypeImpl<I extends Item> extends HolderableBase<WrappedItemCodec<I>> implements ItemTypeTypeNMS<I> {

    public ItemTypeTypeImpl(Holder<WrappedItemCodec<I>> wrappedCodecHolder) {
        super(wrappedCodecHolder);
    }

    @Override
    public WrappedItemCodec<I> getWrappedCodec() {
        return this.getHandle();
    }

    public static <I extends Item> ItemTypeTypeImpl<I> of(Holder<WrappedItemCodec<?>> wrappedCodecHolder) {
        return new ItemTypeTypeImpl<>((Holder<WrappedItemCodec<I>>) (Holder) wrappedCodecHolder);
    }

}
