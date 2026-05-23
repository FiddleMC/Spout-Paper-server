package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#sapling}.
 */
public class SaplingRequestProcessor extends StandardBlockTypeRequestProcessor {

    public SaplingRequestProcessor(FromToBlockTypeRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<FromToBlockTypeRequestBuilderImpl, RequestBasedResult>.FillPromise kickoff) {
        return super.constructFillPromise(
            kickoff
                .then(this.attemptToClaimStatesFillPromiseStateByState(SAPLING_PROXY_STATES::get, false))
        );
    }

    @Override
    protected List<Block> getFallbackBlocks() {
        return SAFE_FALLBACK_BLOCKS;
    }

    /**
     * A new {@link DynamicClaimableStates} instance,
     * for {@link BlockState}s that can be attempted to be claimed as sapling proxies.
     */
    public static final DynamicClaimableStates SAPLING_PROXY_STATES = SingletonBlockStateDynamicClaimableStates.forProxy(() -> StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false)
        .filter(block -> block.getClass().equals(SaplingBlock.class))
        .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
        .toList()
    );

    public static final List<Block> SAFE_FALLBACK_BLOCKS = Stream.concat(Stream.of(Blocks.OAK_SAPLING), StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof SaplingBlock)).distinct().toList();

}
