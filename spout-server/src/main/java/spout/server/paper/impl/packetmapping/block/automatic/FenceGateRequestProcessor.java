package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.stream.StreamSupport;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#fenceGate}.
 */
public class FenceGateRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public FenceGateRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(PROMISE_GETTER.get(this))
            // TODO try to claim duplicate open states
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link FillPromiseGetter} instance,
     * for {@link BlockState}s that can be attempted to be claimed as fence gate proxies.
     */
    public static final FillPromiseGetter<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> PROMISE_GETTER = claimStatesForAllStatesAtOnceForBlockStatesByDynamicProperties(
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof FenceGateBlock).toList(),
        BlockStateProperties.HORIZONTAL_FACING,
        BlockStateProperties.IN_WALL,
        BlockStateProperties.OPEN
    );

}
