package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.stream.StreamSupport;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#trapdoor}.
 */
public class TrapdoorRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public TrapdoorRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(PROMISE_GETTER.get(this))
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link FillPromiseGetter} instance,
     * for {@link BlockState}s that can be attempted to be claimed as trapdoor proxies.
     */
    public static final FillPromiseGetter<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> PROMISE_GETTER = claimStatesForAllStatesAtOnceForBlockStatesByDynamicProperties(
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof TrapDoorBlock).toList(),
        BlockStateProperties.HORIZONTAL_FACING,
        BlockStateProperties.HALF,
        BlockStateProperties.OPEN,
        BlockStateProperties.WATERLOGGED
    );

}
