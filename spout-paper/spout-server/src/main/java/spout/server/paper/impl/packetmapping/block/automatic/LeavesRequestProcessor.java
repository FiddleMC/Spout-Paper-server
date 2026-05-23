package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A {@link RequestProcessor} for {@link AutomaticBlockMappingsImpl#leaves}.
 */
public class LeavesRequestProcessor extends FilledArrayResultRequestProcessor<LeavesRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> {

    public LeavesRequestProcessor(LeavesRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected FilledArrayResultRequestProcessor<LeavesRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult>.FillPromise constructFillPromise(final FilledArrayResultRequestProcessor<LeavesRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult>.FillPromise kickoff) {
        return kickoff
            .then(this.request.tintedOrInfer() ? TINTED_CLAIM_PROXY_PROMISE_GETTER.get(this) : UNTINTED_CLAIM_PROXY_PROMISE_GETTER.get(this))
            .then(this.request.tintedOrInfer() ? TINTED_CLAIM_FALLBACK_PROMISE_GETTER.get(this) : UNTINTED_CLAIM_FALLBACK_PROMISE_GETTER.get(this))
            .then(new BlockFallbackFillPromise(this.request.fallback));
    }

    /**
     * A new {@link FillPromiseGetter} instance,
     * for {@link BlockState}s that can be attempted to be claimed as tinted leaves proxies.
     */
    public static final FillPromiseGetter<LeavesRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> TINTED_CLAIM_PROXY_PROMISE_GETTER = claimProxyStatesForAllStatesAtOnceForBlockStatesByDynamicProperties(
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof TintedParticleLeavesBlock).toList(),
        BlockStateProperties.WATERLOGGED
    );

    /**
     * A new {@link FillPromiseGetter} instance,
     * for {@link BlockState}s that can be attempted to be claimed as untinted leaves proxies.
     */
    public static final FillPromiseGetter<LeavesRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> UNTINTED_CLAIM_PROXY_PROMISE_GETTER = claimProxyStatesForAllStatesAtOnceForBlockStatesByDynamicProperties(
        StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof UntintedParticleLeavesBlock).toList(),
        BlockStateProperties.WATERLOGGED
    );

    public static final FillPromiseGetter<LeavesRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> TINTED_CLAIM_FALLBACK_PROMISE_GETTER = claimFallbackStatesForAllStatesAtOnceByBlock(
        Stream.concat(Stream.of(Blocks.OAK_LEAVES), StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof TintedParticleLeavesBlock)).distinct().toList()
    );

    public static final FillPromiseGetter<LeavesRequestBuilderImpl, ArrayResultRequestProcessor.RequestBasedResult> UNTINTED_CLAIM_FALLBACK_PROMISE_GETTER = claimFallbackStatesForAllStatesAtOnceByBlock(
        Stream.concat(Stream.of(Blocks.PALE_OAK_LEAVES), StreamSupport.stream(VanillaOnlyBlockRegistry.get().spliterator(), false).filter(block -> block instanceof UntintedParticleLeavesBlock)).distinct().toList()
    );

}
