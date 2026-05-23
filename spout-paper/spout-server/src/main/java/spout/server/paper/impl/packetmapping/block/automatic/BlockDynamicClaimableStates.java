package spout.server.paper.impl.packetmapping.block.automatic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import spout.server.paper.impl.packetmapping.block.claim.ResourcePackBlockStateClaimsImpl;
import spout.server.paper.impl.packetmapping.block.claim.VisualDuplicatesImpl;

/**
 * A producer of {@link SortedClaimableStates} instances
 * that is based off an initial list of {@link Block}s that are claimed whole.
 *
 * <p>
 * Each {@link SortedClaimableStates#get} call will return an array with the same size
 * as the corresponding {@link StateDefinition#getPossibleStates()}.
 * </p>
 */
public class BlockDynamicClaimableStates implements DynamicClaimableStates {

    /**
     * The backing value for {@link #get)},
     * or null if not initialized yet.
     */
    private @Nullable LinkedHashSet<Block> values;

    /**
     * A supplier of the initial blocks,
     * or null if dereferenced.
     *
     * <p>
     * The {@link Block}s it returns must all have the exact same block state properties.
     * </p>
     */
    private @Nullable Supplier<Collection<Block>> initialBlocksSupplier;

    /**
     * A supplier of the preferred block,
     * or null if there is no preferred block,
     * or null if dereferenced.
     */
    private @Nullable Supplier<Block> preferredBlockSupplier;

    /**
     * Whether these states are fallback states.
     */
    private final boolean isFallback;

    private BlockDynamicClaimableStates(Supplier<Collection<Block>> initialBlocksSupplier, Supplier<Block> preferredBlockSupplier, boolean isFallback) {
        this.initialBlocksSupplier = initialBlocksSupplier;
        this.preferredBlockSupplier = preferredBlockSupplier;
        this.isFallback = isFallback;
    }

    @Override
    public SortedClaimableStates get(BlockState from) {
        if (this.values == null) {
            Collection<Block> initialBlocks = this.initialBlocksSupplier.get();
            this.initialBlocksSupplier = null;
            this.values = new LinkedHashSet<>(initialBlocks.size() + (this.preferredBlockSupplier != null ? 1 : 0));
            if (this.preferredBlockSupplier != null) {
                this.values.add(this.preferredBlockSupplier.get());
                this.preferredBlockSupplier = null;
            }
            initialBlocks.stream()
                .filter(block -> block.getStateDefinition().getPossibleStates().stream().noneMatch(state -> this.isFallback ? ResourcePackBlockStateClaimsImpl.get().isClaimedNonVanilla(state) : ResourcePackBlockStateClaimsImpl.get().isClaimed(state)))
                .sorted(VisualDuplicatesImpl.VisualDuplicateGroupImpl.BLOCK_COMPARATOR)
                .forEach(this.values::add);
            if (!this.isFallback) {
                ResourcePackBlockStateClaimsImpl.get().registerClaimListener(state -> this.values.remove(VanillaOnlyBlockStateRegistry.get().byId(state).getBlock()));
            }
        }
        return SortedClaimableStates.of(from, this.values.stream().map(block -> block.getStateDefinition().getPossibleStates().toArray(BlockState[]::new)).toArray(BlockState[][]::new));
    }

    public static BlockDynamicClaimableStates forProxy(Supplier<Collection<Block>> initialBlocksSupplier) {
        return new BlockDynamicClaimableStates(initialBlocksSupplier, null, false);
    }

    public static BlockDynamicClaimableStates forFallback(Supplier<Collection<Block>> initialBlocksSupplier, Supplier<Block> preferredBlockSupplier) {
        return new BlockDynamicClaimableStates(initialBlocksSupplier, preferredBlockSupplier, true);
    }

}
