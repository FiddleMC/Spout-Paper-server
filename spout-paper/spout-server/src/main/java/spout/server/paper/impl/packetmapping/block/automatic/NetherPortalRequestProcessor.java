package spout.server.paper.impl.packetmapping.block.automatic;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#netherPortal}.
 */
public class NetherPortalRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public NetherPortalRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        BlockState[] fromStates = this.request.fromStates();
        IntList xResultIndices = new IntArrayList(1);
        IntList zResultIndices = new IntArrayList(1);
        for (int i = 0; i < fromStates.length; i++) {
            Optional<Direction.Axis> optionalAxis = fromStates[i].getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS);
            if (optionalAxis.isPresent()) {
                Direction.Axis axis = optionalAxis.get();
                if (axis == Direction.Axis.X) {
                    xResultIndices.add(i);
                } else if (axis == Direction.Axis.Z) {
                    zResultIndices.add(i);
                }
            }
        }
        int[] xResultIndicesArray = xResultIndices.toIntArray();
        int[] zResultIndicesArray = zResultIndices.toIntArray();
        int[] xResultIndicesIndices = IntStream.range(0, xResultIndicesArray.length).toArray();
        int[] zResultIndicesIndices = IntStream.range(0, zResultIndicesArray.length).toArray();
        return kickoff
            .then(new AttemptToClaimStatesFillPromise(xResultIndicesArray, X_NETHER_PORTAL_PROXY_STATES::get, $ -> xResultIndicesIndices, false))
            .then(new AttemptToClaimStatesFillPromise(zResultIndicesArray, Z_NETHER_PORTAL_PROXY_STATES::get, $ -> zResultIndicesIndices, false))
            .then(CLAIM_FALLBACK_PROMISE_GETTER.get(this))
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    private static List<BlockState> getFenceGateProxyStates(Direction.Axis axis) {
        return StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false)
            .filter(block -> block instanceof FenceGateBlock)
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(state -> state.getValue(BlockStateProperties.OPEN))
            .filter(state -> !state.getValue(BlockStateProperties.IN_WALL))
            .filter(state -> state.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis() == axis)
            .toList();
    }

    /**
     * A new {@link DynamicClaimableStates} instance,
     * for {@link BlockState}s that can be attempted to be claimed as x-axis nether portal proxies.
     */
    public static final DynamicClaimableStates X_NETHER_PORTAL_PROXY_STATES = SingletonBlockStateDynamicClaimableStates.forProxy(() -> getFenceGateProxyStates(Direction.Axis.Z));

    /**
     * A new {@link DynamicClaimableStates} instance,
     * for {@link BlockState}s that can be attempted to be claimed as z-axis nether portal proxies.
     */
    public static final DynamicClaimableStates Z_NETHER_PORTAL_PROXY_STATES = SingletonBlockStateDynamicClaimableStates.forProxy(() -> getFenceGateProxyStates(Direction.Axis.X));

    public static final FillPromiseGetter<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> CLAIM_FALLBACK_PROMISE_GETTER = claimFallbackStatesForAllStatesAtOnceByBlock(
        List.of(Blocks.NETHER_PORTAL)
    );

}
