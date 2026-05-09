package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.List;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#brushable}.
 */
public class RotatedPillarRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public RotatedPillarRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlock(ROTATED_PILLAR_PROXY_BLOCKS::get, Blocks.QUARTZ_PILLAR))
            .then(attemptToClaimStatesFillPromiseStateByState(FullBlockRequestProcessor.FULL_BLOCK_PROXY_STATES::get))
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link DynamicClaimableStates} instance,
     * for {@link Block}s that can be attempted to be claimed as rotated pillar proxies.
     */
    public static final DynamicClaimableStates ROTATED_PILLAR_PROXY_BLOCKS = new BlockDynamicClaimableStates(() -> List.of(
        // Infested
        Blocks.DEEPSLATE,
        Blocks.INFESTED_DEEPSLATE
    ));

}
