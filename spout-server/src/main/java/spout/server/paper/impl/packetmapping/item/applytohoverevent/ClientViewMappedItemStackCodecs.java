package spout.server.paper.impl.packetmapping.item.applytohoverevent;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import spout.server.paper.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal;
import spout.server.paper.impl.packetmapping.item.ItemMappingsImpl;

import java.util.function.Function;

/**
 * Holder for mapped {@link ItemStack} codecs.
 */
public final class ClientViewMappedItemStackCodecs {

    private ClientViewMappedItemStackCodecs() {
        throw new UnsupportedOperationException();
    }

    /**
     * A modified version of {@link ItemStack#MAP_CODEC}, which maps the item according to the
     * {@link ClientViewLookupThreadLocal#getThreadLocalClientViewOrFallback}.
     */
    public static final MapCodec<ItemStack> MAP_CODEC = ItemStack.MAP_CODEC.xmap(
        Function.identity(), // Used by io.papermc.paper.adventure.WrapperAwareSerializer#deserialize
        itemStack -> ItemMappingsImpl.get().applyGenerically(itemStack)
    );

    /**
     * A modified version of {@link ItemStackTemplate#MAP_CODEC}, which maps the item according to the
     * {@link ClientViewLookupThreadLocal#getThreadLocalClientViewOrFallback}.
     */
    public static final MapCodec<ItemStackTemplate> TEMPLATE_MAP_CODEC = ItemStackTemplate.MAP_CODEC.xmap(
        Function.identity(), // Used by io.papermc.paper.adventure.WrapperAwareSerializer#deserialize
        template -> ItemMappingsImpl.get().applyGenerically(template)
    );

}
