package spout.common.moredatadriven.minecraft.itemtype;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import spout.common.moredatadriven.minecraft.item.SpoutDataDrivenItem;

/**
 * An item type, which represents an implementation of a {@link Item}.
 */
public interface SpoutItemType {

    Identifier getIdentifier();

    MapCodec<SpoutDataDrivenItem> getCodec();

    <T> DataResult<? extends Item> decodeItemFromInput(DynamicOps<T> dynamicOps, MapLike<T> mapLike);

}
