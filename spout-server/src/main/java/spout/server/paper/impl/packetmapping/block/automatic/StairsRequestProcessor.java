package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.List;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#stairs}.
 */
public class StairsRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public StairsRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlock(STAIRS_PROXY_BLOCKS::get, Blocks.STONE_STAIRS))
            // TODO optionally claim inner/outer stair duplicates
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link DynamicClaimableStates} instance,
     * for {@link Block}s that can be attempted to be claimed as stairs proxies.
     */
    public static final DynamicClaimableStates STAIRS_PROXY_BLOCKS = new BlockDynamicClaimableStates(() -> List.of(
        // Copper
        Blocks.CUT_COPPER_STAIRS,
        Blocks.EXPOSED_CUT_COPPER_STAIRS,
        Blocks.OXIDIZED_CUT_COPPER_STAIRS,
        Blocks.WEATHERED_CUT_COPPER_STAIRS,
        Blocks.WAXED_CUT_COPPER_STAIRS,
        Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS,
        Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
        Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS
    ));

}
