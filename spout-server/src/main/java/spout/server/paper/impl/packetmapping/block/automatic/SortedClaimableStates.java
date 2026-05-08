package spout.server.paper.impl.packetmapping.block.automatic;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * A utility class representing claimable states.
 */
public interface SortedClaimableStates {

    /**
     * @return The number of claims that can be made with these states.
     */
    int claims();

    /**
     * @param claimIndex An index between 0 (inclusive) and {@link #claims()} (exclusive).
     * @return The block states to be claimed for the {@code claimIndex}'th request.
     */
    BlockState[] get(int claimIndex);

    /**
     * A common base for {@link Direct} and {@link Singletons},
     * that stores the return value for {@link #get} as an array of {@link I}s.
     */
    abstract class Abstract<I> implements SortedClaimableStates {

        /**
         * The {@link #candidates} will only be internally sorted when this many requests have been made
         * using {@link #get}.
         */
        protected static final int GET_WITHOUT_SORTING_LIMIT = 5;

        /**
         * The {@link #candidates} will only be internally sorted when {@link #candidates} has at least this many elements.
         */
        protected static final int MINIMUM_SIZE_TO_SORT = 10;

        /**
         * A reference {@link BlockState} for which these states are being claimed:
         * specifically, it must be usable as the {@code from} state for
         * {@linkplain BlockRequestProcessUtils#compareProxyCandidates proxy candidate comparisons}
         * for the first {@link BlockState} in every simultaneous claim request (aka
         * the element at index 0 in each inner array of {@link #candidates}).
         */
        protected final BlockState from;

        /**
         * An array of claimable states.
         *
         * <p>
         * The array represents the order of claim attempts.
         * </p>
         */
        protected final @Nullable I[] candidates;

        protected Abstract(BlockState from, I[] candidates) {
            this.from = from;
            this.candidates = candidates;
        }

        @Override
        public int claims() {
            return this.candidates.length;
        }

        protected abstract BlockState getFirstCandidate(int arrayIndex);

        protected abstract BlockState[] getCandidates(int arrayIndex);

        protected abstract BlockState getFirstCandidate(I arrayValue);

        @Override
        public BlockState[] get(int claimIndex) {
            if (claimIndex < GET_WITHOUT_SORTING_LIMIT || this.candidates.length < MINIMUM_SIZE_TO_SORT) {
                int best = 0;
                for (int i = 1; i < this.candidates.length; i++) {
                    if (this.candidates[best] == null || (this.candidates[i] != null && BlockRequestProcessUtils.compareProxyCandidates(this.from, this.getFirstCandidate(best), this.getFirstCandidate(i)) > 0)) {
                        best = i;
                    }
                }
                BlockState[] toReturn = this.getCandidates(best);
                this.candidates[best] = null;
                return toReturn;
            }
            if (claimIndex == GET_WITHOUT_SORTING_LIMIT) {
                // Sort before getting
                ObjectArrays.stableSort(this.candidates, (states1, states2) -> {
                    if (states1 == null) {
                        return states2 == null ? 0 : 1;
                    }
                    if (states2 == null) {
                        return -1;
                    }
                    return BlockRequestProcessUtils.compareProxyCandidates(this.from, this.getFirstCandidate(states1), this.getFirstCandidate(states2));
                });
            }
            // Get directly after sorting
            BlockState[] toReturn = this.getCandidates(claimIndex - GET_WITHOUT_SORTING_LIMIT);
            this.candidates[claimIndex - GET_WITHOUT_SORTING_LIMIT] = null;
            return toReturn;
        }

    }

    class Direct extends Abstract<BlockState[]> {

        private Direct(BlockState from, BlockState[][] candidates) {
            super(from, candidates);
        }

        @Override
        protected BlockState getFirstCandidate(int arrayIndex) {
            return this.candidates[arrayIndex][0];
        }

        @Override
        protected BlockState[] getCandidates(int arrayIndex) {
            return this.candidates[arrayIndex];
        }

        @Override
        protected BlockState getFirstCandidate(BlockState[] arrayValue) {
            return arrayValue[0];
        }

    }

    class Singletons extends Abstract<BlockState> {

        private Singletons(BlockState from, BlockState[] candidates) {
            super(from, candidates);
        }

        @Override
        protected BlockState getFirstCandidate(int arrayIndex) {
            return this.candidates[arrayIndex];
        }

        @Override
        protected BlockState[] getCandidates(int arrayIndex) {
            return new BlockState[]{this.candidates[arrayIndex]};
        }

        @Override
        protected BlockState getFirstCandidate(BlockState arrayValue) {
            return arrayValue;
        }

    }

    SortedClaimableStates EMPTY = new SortedClaimableStates() {

        @Override
        public int claims() {
            return 0;
        }

        @Override
        public BlockState[] get(int claimIndex) {
            throw new IllegalArgumentException();
        }

    };

    static SortedClaimableStates of(BlockState from, BlockState[][] candidates) {
        if (candidates.length == 0) {
            return EMPTY;
        }
        return new Direct(from, candidates);
    }

    static SortedClaimableStates of(BlockState from, BlockState[] candidates) {
        if (candidates.length == 0) {
            return EMPTY;
        }
        return new Singletons(from, candidates);
    }

}
