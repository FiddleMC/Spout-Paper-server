package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.server.paper.impl.packetmapping.block.claim.ResourcePackBlockStateClaimsImpl;
import spout.server.paper.impl.packetmapping.block.claim.VisualDuplicatesImpl;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * A producer of {@link SortedClaimableStates} instances
 * that is based off an initial list of {@link BlockState}s.
 *
 * <p>
 * Each {@link SortedClaimableStates#get} call will return an array of length 1.
 * </p>
 */
public class SingletonBlockStateDynamicClaimableStates implements DynamicClaimableStates {

    /**
     * The backing value for {@link #get)},
     * or null if not initialized yet.
     */
    private @Nullable LinkedHashMap<Integer, BlockState> values;

    /**
     * A supplier of the initial states,
     * or null if dereferenced.
     */
    private @Nullable Supplier<Collection<BlockState>> initialStatesSupplier;

    /**
     * A supplier of the preferred state,
     * or null if there is no preferred state,
     * or null if dereferenced.
     */
    private @Nullable Supplier<BlockState> preferredStateSupplier;

    /**
     * Whether these states are fallback states.
     */
    private final boolean isFallback;

    private SingletonBlockStateDynamicClaimableStates(Supplier<Collection<BlockState>> initialStatesSupplier, Supplier<BlockState> preferredStateSupplier, boolean isFallback) {
        this.initialStatesSupplier = initialStatesSupplier;
        this.preferredStateSupplier = preferredStateSupplier;
        this.isFallback = isFallback;
    }

    @Override
    public SortedClaimableStates get(BlockState from) {
        if (this.values == null) {
            Collection<BlockState> initialStates = this.initialStatesSupplier.get();
            this.initialStatesSupplier = null;
            this.values = new LinkedHashMap<>(initialStates.size() + (this.preferredStateSupplier != null ? 1 : 0));
            if (this.preferredStateSupplier != null) {
                BlockState preferredState = this.preferredStateSupplier.get();
                this.values.put(preferredState.indexInVanillaOnlyBlockStateRegistry, preferredState);
                this.preferredStateSupplier = null;
            }
            initialStates.stream()
                .filter(state -> !(this.isFallback ? ResourcePackBlockStateClaimsImpl.get().isClaimedNonVanilla(state) : ResourcePackBlockStateClaimsImpl.get().isClaimed(state)))
                .sorted(VisualDuplicatesImpl.VisualDuplicateGroupImpl.STATE_COMPARATOR)
                .forEach(state -> this.values.put(state.indexInVanillaOnlyBlockStateRegistry, state));
            ResourcePackBlockStateClaimsImpl.get().registerClaimListener(this.values::remove);
        }
        return SortedClaimableStates.of(from, this.values.values().toArray(BlockState[]::new));
    }

    public static SingletonBlockStateDynamicClaimableStates forProxy(Supplier<Collection<BlockState>> initialStatesSupplier) {
        return new SingletonBlockStateDynamicClaimableStates(initialStatesSupplier, null, false);
    }

    public static SingletonBlockStateDynamicClaimableStates forFallback(Supplier<Collection<BlockState>> initialStatesSupplier, Supplier<BlockState> preferredStateSupplier) {
        return new SingletonBlockStateDynamicClaimableStates(initialStatesSupplier, preferredStateSupplier, true);
    }

}
