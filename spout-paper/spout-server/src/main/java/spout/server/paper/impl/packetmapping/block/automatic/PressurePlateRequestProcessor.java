package spout.server.paper.impl.packetmapping.block.automatic;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#pressurePlate}.
 */
public class PressurePlateRequestProcessor extends FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public PressurePlateRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        BlockState[] fromStates = this.request.fromStates();
        IntList downResultIndices = new IntArrayList(fromStates.length - 1);
        IntList upResultIndices = new IntArrayList(1);
        for (int i = 0; i < fromStates.length; i++) {
            if (fromStates[i].getValueOrElse(BlockStateProperties.POWER, 0) != 0 || fromStates[i].getValueOrElse(BlockStateProperties.POWERED, false)) {
                downResultIndices.add(i);
            } else {
                upResultIndices.add(i);
            }
        }
        int[] downResultIndicesArray = downResultIndices.toIntArray();
        int[] upResultIndicesArray = upResultIndices.toIntArray();
        int[] downResultIndicesIndices = IntStream.range(0, downResultIndicesArray.length).toArray();
        int[] upResultIndicesIndices = IntStream.range(0, upResultIndicesArray.length).toArray();
        return kickoff
            .then(new AttemptToClaimStatesFillPromise(downResultIndicesArray, PRESSURE_PLATE_PROXY_STATES::get, $ -> downResultIndicesIndices, false))
            .then(new AttemptToClaimStatesFillPromise(upResultIndicesArray, PRESSURE_PLATE_PROXY_STATES::get, $ -> upResultIndicesIndices, false))
            .then(new AttemptToClaimStatesFillPromise(downResultIndicesArray, POWERED_PRESSURE_PLATE_FALLBACK_STATES::get, $ -> downResultIndicesIndices, true))
            .then(new AttemptToClaimStatesFillPromise(upResultIndicesArray, UNPOWERED_PRESSURE_PLATE_FALLBACK_STATES::get, $ -> upResultIndicesIndices, true))
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link DynamicClaimableStates} instance,
     * for {@link BlockState}s that can be attempted to be claimed as powered pressure plate proxies.
     */
    public static final DynamicClaimableStates PRESSURE_PLATE_PROXY_STATES = SingletonBlockStateDynamicClaimableStates.forProxy(() -> StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false)
        .filter(block -> block instanceof WeightedPressurePlateBlock)
        .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
        .filter(state -> state.getValue(BlockStateProperties.POWER) != 0)
        .toList());

    public static final DynamicClaimableStates POWERED_PRESSURE_PLATE_FALLBACK_STATES = SingletonBlockStateDynamicClaimableStates.forProxy(() -> Stream.concat(
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false)
            .filter(block -> block instanceof WeightedPressurePlateBlock)
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(state -> state.getValue(BlockStateProperties.POWER) != 0),
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false)
            .filter(block -> block instanceof PressurePlateBlock)
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(state -> state.getValue(BlockStateProperties.POWERED))
    ).toList());

    public static final DynamicClaimableStates UNPOWERED_PRESSURE_PLATE_FALLBACK_STATES = SingletonBlockStateDynamicClaimableStates.forProxy(() -> Stream.concat(
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false)
            .filter(block -> block instanceof WeightedPressurePlateBlock)
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(state -> state.getValue(BlockStateProperties.POWER) == 0),
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false)
            .filter(block -> block instanceof PressurePlateBlock)
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .filter(state -> !state.getValue(BlockStateProperties.POWERED))
    ).toList());

}
