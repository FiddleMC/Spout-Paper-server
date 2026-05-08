package spout.server.paper.impl.packetmapping.block.automatic;

import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#ladder}.
 */
public class LadderRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public LadderRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

}
