package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.List;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl} requests
 * that are fulfilled using full blocks but backed by a block rather than a block state.
 */
public class FullBlockBlockTypeRequestProcessor extends StandardBlockTypeRequestProcessor {

    public FullBlockBlockTypeRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return super.constructFillPromise(
            kickoff
                .then(attemptToClaimStatesFillPromiseStateByState(FullBlockRequestProcessor.FULL_BLOCK_PROXY_STATES::get, false))
        );
    }

    @Override
    protected List<Block> getFallbackBlocks() {
        return SAFE_FALLBACK_BLOCKS;
    }

    public static final List<Block> SAFE_FALLBACK_BLOCKS = List.of(Blocks.STONE, Blocks.OAK_PLANKS, Blocks.DIRT, Blocks.SAND, Blocks.GLASS, Blocks.HAY_BLOCK);

}
