package spout.server.paper.impl.packetmapping.block;

import net.minecraft.core.IdMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.clientview.model.ClientView;
import spout.server.paper.api.packetmapping.block.BlockMappingFunctionContext;
import spout.server.paper.impl.clientview.lookup.packethandling.ClientViewLookupThreadLocal;
import java.util.Iterator;

/**
 * An implementation of {@link IdMap} that defers to
 * {@link Block#BLOCK_STATE_REGISTRY}, but applies block mappings first.
 *
 */
public final class MappedBlockStateRegistry implements IdMap<BlockState> {

    private static @Nullable MappedBlockStateRegistry INSTANCE;

    public static MappedBlockStateRegistry get() {
        if (INSTANCE == null) {
            INSTANCE = new MappedBlockStateRegistry();
        }
        return INSTANCE;
    }

    private MappedBlockStateRegistry() {
    }

    @Override
    public int getId(BlockState thing) {
        BlockMappingFunctionContext context = new BlockMappingFunctionContextImpl(ClientViewLookupThreadLocal.getThreadLocalClientViewOrFallback());
        BlockState mappedThing = BlockMappingsImpl.get().apply(thing, context);
        return Block.BLOCK_STATE_REGISTRY.getId(mappedThing);
    }

    /**
     * Analogous to {@link Block#getId(BlockState)}.
     */
    public int getIdNullable(@Nullable BlockState thing) {
        return this.getIdNullable(thing, null);
    }

    /**
     * Analogous to {@link Block#getId(BlockState)}.
     */
    public int getIdNullable(@Nullable BlockState thing, @Nullable ServerPlayer player) {
        if (thing == null) {
            return 0;
        }

        ClientView clientView = player != null ? player.getClientView() : null;
        if (clientView == null) {
            clientView = ClientViewLookupThreadLocal.getThreadLocalClientViewOrFallback();
        }
        BlockMappingFunctionContext context = new BlockMappingFunctionContextImpl(clientView);
        BlockState mappedThing = BlockMappingsImpl.get().apply(thing, context);
        int id = Block.BLOCK_STATE_REGISTRY.getId(mappedThing);
        return id == -1 ? 0 : id;
    }

    @Override
    public @Nullable BlockState byId(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<BlockState> iterator() {
        throw new UnsupportedOperationException();
    }

}
