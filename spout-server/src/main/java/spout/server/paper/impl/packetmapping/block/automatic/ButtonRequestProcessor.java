package spout.server.paper.impl.packetmapping.block.automatic;

import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#button}.
 */
public class ButtonRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public ButtonRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

}
