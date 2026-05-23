package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl} requests
 * that are backed by a block.
 */
public abstract class StandardBlockTypeRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public StandardBlockTypeRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(this.getFallbackFillPromise())
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    protected abstract List<Block> getFallbackBlocks();

    protected Block getReferenceBlock() {
        return this.getFallbackBlocks().get(0);
    }

    protected FillPromise getFallbackFillPromise() {
        return this.attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlock(BlockDynamicClaimableStates.forFallback(this::getFallbackBlocks, () -> this.request.fallback)::get, this.getReferenceBlock(),true);
    }

    public static BiFunction<FromToBlockTypeRequestBuilderImpl, BlockMappingsComposeEventImpl, StandardBlockTypeRequestProcessor> withFallbackBlocks(Block... fallbackBlocks) {
        return withFallbackBlocks(Arrays.asList(fallbackBlocks));
    }

    public static BiFunction<FromToBlockTypeRequestBuilderImpl, BlockMappingsComposeEventImpl, StandardBlockTypeRequestProcessor> withFallbackBlocks(List<Block> fallbackBlocks) {
        class WithGivenFallbackBlocks extends StandardBlockTypeRequestProcessor {

            public WithGivenFallbackBlocks(final FromToBlockTypeRequestBuilderImpl request, final BlockMappingsComposeEventImpl event) {
                super(request, event);
            }

            @Override
            protected List<Block> getFallbackBlocks() {
                return fallbackBlocks;
            }

        }
        return WithGivenFallbackBlocks::new;
    }

}
