package spout.server.paper.impl.moredatadriven.paper.registry.type;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import spout.common.moredatadriven.minecraft.itemtype.SpoutItemType;
import spout.server.paper.api.moredatadriven.paper.registry.type.ItemTypeType;
import spout.server.paper.api.moredatadriven.paper.registry.type.nms.ItemTypeTypeNMS;

/**
 * The implementation for {@link ItemTypeType}.
 */
public final class ItemTypeTypeImpl extends HolderableBase<SpoutItemType> implements ItemTypeTypeNMS {

    public ItemTypeTypeImpl(Holder<SpoutItemType> wrappedCodecHolder) {
        super(wrappedCodecHolder);
    }

    @Override
    public Identifier getIdentifier() {
        return this.holder.value().getIdentifier();
    }

    public static ItemTypeTypeImpl of(Holder<SpoutItemType> minecraftHolder) {
        return new ItemTypeTypeImpl(minecraftHolder);
    }

}
