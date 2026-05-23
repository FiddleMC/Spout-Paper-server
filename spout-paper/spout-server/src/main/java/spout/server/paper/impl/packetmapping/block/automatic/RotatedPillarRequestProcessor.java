package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
            .then(this.attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlock(ROTATED_PILLAR_PROXY_BLOCKS::get, Blocks.QUARTZ_PILLAR, false))
            .then(this.attemptToClaimStatesFillPromiseStateByState(FullBlockRequestProcessor.FULL_BLOCK_PROXY_STATES::get, false))
            .then(CLAIM_FALLBACK_PROMISE_GETTER.get(this))
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link DynamicClaimableStates} instance,
     * for {@link Block}s that can be attempted to be claimed as rotated pillar proxies.
     */
    public static final DynamicClaimableStates ROTATED_PILLAR_PROXY_BLOCKS = BlockDynamicClaimableStates.forProxy(() -> List.of(
        // Infested
        Blocks.DEEPSLATE,
        Blocks.INFESTED_DEEPSLATE
    ));

    public static final FillPromiseGetter<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> CLAIM_FALLBACK_PROMISE_GETTER = claimFallbackStatesForAllStatesAtOnceByBlock(
        Stream.concat(Stream.of(Blocks.QUARTZ_PILLAR), StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof RotatedPillarBlock)).distinct().toList()
    );

}
