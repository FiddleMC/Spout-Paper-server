package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.stream.StreamSupport;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#door}.
 */
public class DoorRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {// TODO use powered vs unpowered states, also

    public DoorRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(PROMISE_GETTER.get(this))
            // TODO claim duplicate states of other door blocks (based on hinge and direction)
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link FillPromiseGetter} instance,
     * for {@link BlockState}s that can be attempted to be claimed as door proxies.
     */
    public static final FillPromiseGetter<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> PROMISE_GETTER = claimStatesForAllStatesAtOnceForBlockStatesByDynamicProperties(
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof DoorBlock).toList(),
        BlockStateProperties.HORIZONTAL_FACING,
        BlockStateProperties.DOUBLE_BLOCK_HALF,
        BlockStateProperties.DOOR_HINGE,
        BlockStateProperties.OPEN
    );

}
