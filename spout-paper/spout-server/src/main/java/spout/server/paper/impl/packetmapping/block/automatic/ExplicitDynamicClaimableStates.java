package spout.server.paper.impl.packetmapping.block.automatic;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;
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
public class ExplicitDynamicClaimableStates implements DynamicClaimableStates {

    /**
     * A set that stores the {@link BlockState#indexInVanillaOnlyBlockStateRegistry}
     * for any {@link BlockState} in {@link #values},
     * or null if not initialized yet.
     */
    private @Nullable IntSet statesSet;

    /**
     * The backing value for {@link #get)},
     * or null if not initialized yet.
     */
    private @Nullable LinkedList<BlockState[]> values;

    /**
     * A supplier of the initial states,
     * or null if dereferenced.
     *
     * <p>
     * The {@link Block}s it returns must all have the exact same block state properties.
     * </p>
     */
    private @Nullable Supplier<Collection<BlockState[]>> initialBlockStatesSupplier;

    /**
     * A supplier of the preferred states,
     * or null if there is no preferred states,
     * or null if dereferenced.
     */
    private @Nullable Supplier<BlockState[]> preferredBlockStatesSupplier;

    /**
     * Whether these states are fallback states.
     */
    private final boolean isFallback;

    public ExplicitDynamicClaimableStates(Supplier<Collection<BlockState[]>> initialBlockStatesSupplier, Supplier<BlockState[]> preferredBlockStatesSupplier, boolean isFallback) {
        this.initialBlockStatesSupplier = initialBlockStatesSupplier;
        this.preferredBlockStatesSupplier = preferredBlockStatesSupplier;
        this.isFallback = isFallback;
    }

    @Override
    public SortedClaimableStates get(BlockState from) {
        if (this.values == null) {
            Collection<BlockState[]> initialBlockStates = this.initialBlockStatesSupplier.get();
            this.initialBlockStatesSupplier = null;
            this.values = new LinkedList<>();
            if (this.preferredBlockStatesSupplier != null) {
                this.values.add(this.preferredBlockStatesSupplier.get());
                this.preferredBlockStatesSupplier = null;
            }
            initialBlockStates.stream()
                .filter(states -> Arrays.stream(states).noneMatch(state -> this.isFallback ? ResourcePackBlockStateClaimsImpl.get().isClaimedNonVanilla(state) : ResourcePackBlockStateClaimsImpl.get().isClaimed(state)))
                .sorted(Comparator.comparing(states -> states[0], VisualDuplicatesImpl.VisualDuplicateGroupImpl.STATE_COMPARATOR))
                .forEach(this.values::add);
            if (!this.isFallback) {
                this.statesSet = new IntOpenHashSet();
                initialBlockStates.forEach(states -> {
                    for (BlockState state : states) {
                        this.statesSet.add(state.indexInVanillaOnlyBlockStateRegistry);
                    }
                });
                ResourcePackBlockStateClaimsImpl.get().registerClaimListener(state -> {
                    if (this.statesSet.contains(state)) {
                        Iterator<BlockState[]> iterator = this.values.iterator();
                        while (iterator.hasNext()) {
                            BlockState[] existingStates = iterator.next();
                            boolean contains = false;
                            for (BlockState existingState : existingStates) {
                                if (existingState.indexInVanillaOnlyBlockStateRegistry == state) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (contains) {
                                for (BlockState existingState : existingStates) {
                                    this.statesSet.remove(existingState.indexInVanillaOnlyBlockStateRegistry);
                                }
                                iterator.remove();
                                break;
                            }
                        }
                    }
                });
            }
        }
        return SortedClaimableStates.of(from, this.values.toArray(BlockState[][]::new));
    }

    public static ExplicitDynamicClaimableStates forProxy(Supplier<Collection<BlockState[]>> initialBlockStatesSupplier) {
        return new ExplicitDynamicClaimableStates(initialBlockStatesSupplier, null, false);
    }

    public static ExplicitDynamicClaimableStates forFallback(Supplier<Collection<BlockState[]>> initialBlockStatesSupplier, Supplier<BlockState[]> preferredBlockStatesSupplier) {
        return new ExplicitDynamicClaimableStates(initialBlockStatesSupplier, preferredBlockStatesSupplier, true);
    }

}
