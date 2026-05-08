package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.List;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#slab}.
 */
public class SlabRequestProcessor extends FilledArrayResultRequestProcessor<SlabRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public SlabRequestProcessor(SlabRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<SlabRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<SlabRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlock(SLAB_PROXY_BLOCKS::get, Blocks.STONE_SLAB))
            // TODO claim full block fallbacks
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link DynamicClaimableStates} instance,
     * for {@link Block}s that can be attempted to be claimed as slab proxies.
     */
    public static final DynamicClaimableStates SLAB_PROXY_BLOCKS = new BlockDynamicClaimableStates(() -> List.of(
        // Copper
        Blocks.CUT_COPPER_SLAB,
        Blocks.EXPOSED_CUT_COPPER_SLAB,
        Blocks.OXIDIZED_CUT_COPPER_SLAB,
        Blocks.WEATHERED_CUT_COPPER_SLAB,
        Blocks.WAXED_CUT_COPPER_SLAB,
        Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB,
        Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB,
        Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB,
        // Petrified oak
        Blocks.OAK_SLAB,
        Blocks.PETRIFIED_OAK_SLAB
    ));

}
