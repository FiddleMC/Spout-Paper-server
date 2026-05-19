package spout.common.moredatadriven.minecraft.item;

import net.minecraft.world.item.Item;
import spout.common.moredatadriven.minecraft.itemtype.SpoutItemType;

/**
 * An interface to get the type of some {@link Item}.
 *
 * <p>
 * This is specifically meant to be implemented by {@link Item}.
 * </p>
 */
public interface ItemWithType {

    SpoutItemType spout$getItemType();

}
