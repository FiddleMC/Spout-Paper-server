package spout.server.paper.impl.packetmapping.block.automatic;

import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#brushable}.
 */
public class BrushableRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public BrushableRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(attemptToClaimStatesFillPromiseStateByState(FullBlockRequestProcessor.FULL_BLOCK_PROXY_STATES::get))
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

}
