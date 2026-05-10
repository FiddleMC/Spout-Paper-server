package spout.server.paper.impl.packetmapping.block.automatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.block.data.BlockData;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.claim.ClaimRequestPriority;
import spout.server.paper.impl.moredatadriven.minecraft.VanillaOnlyBlockStateRegistry;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * An abstract {@link RequestProcessor}
 * that builds its (intermediate) result in a {@link Result}
 * by filling it in steps.
 */
public abstract class FilledArrayResultRequestProcessor<R extends ProxyStatesRequestBuilderImpl, Re extends FilledArrayResultRequestProcessor.Result> extends ArrayResultRequestProcessor<R, Re> {

    protected FilledArrayResultRequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    protected abstract FillPromise constructFillPromise(FillPromise kickoff);

    protected void postFillResult() {
        this.useResult();
    }

    @Override
    protected void processAfterValidateArguments() {
        FillPromise kickoff = new EmptyFillPromise();
        this.constructFillPromise(kickoff).then(this::postFillResult);
        kickoff.run();
    }

    /**
     * A class similar to {@link Future} (but not thread-safe),
     * for filling the {@link #result}.
     */
    public abstract class FillPromise implements Runnable {

        private boolean isDone;
        private @Nullable List<Runnable> postActions = new ArrayList<>(1);

        public <A extends Runnable> A then(A runnable) {
            if (this.isDone) {
                runnable.run();
            } else {
                this.postActions.add(runnable);
            }
            return runnable;
        }

        public void setDone() {
            if (this.isDone) {
                return;
            }
            this.isDone = true;
            for (Runnable runnable : this.postActions) {
                runnable.run();
            }
            this.postActions = null;
        }

    }

    public class EmptyFillPromise extends FillPromise {

        @Override
        public void run() {
            this.setDone();
        }

    }

    public class MultiplePromisesFillPromise extends FillPromise {

        private FillPromise kickOffPromise;

        public MultiplePromisesFillPromise(FillPromise[] promises) {
            for (int i = 1; i < promises.length; i++) {
                promises[i - 1].then(promises[i]);
            }
            promises[promises.length - 1].then(this::setDone);
            this.kickOffPromise = promises[0];
        }

        @Override
        public void run() {
            this.kickOffPromise.run();
        }

    }

    public class AttemptToClaimStatesFillPromise extends FillPromise {

        /**
         * Which indices (in {@link Result#fromStates()}) the claim are made for.
         * If null, then it is assumed to be all the indices.
         */
        protected final int @Nullable [] resultIndicesToClaimFor;

        /**
         * A function of {@link SortedClaimableStates} for the claims (given the corresponding {@code from}),
         * or null if dereferenced.
         */
        protected @Nullable Function<BlockState, SortedClaimableStates> claimableStatesFunction;

        /**
         * The {@link SortedClaimableStates},
         * or null if not cached (computed from {@link #claimableStatesFunction}) yet.
         */
        protected @Nullable SortedClaimableStates claimableStates;

        /**
         * The {@link ClaimRequestPriority} for any made claims, or null if not cached yet.
         */
        protected @Nullable ClaimRequestPriority priority;

        /**
         * The index of the current claim attempt (i.e. the index passed to {@link SortedClaimableStates#get}.
         */
        private int claimIndex;

        /**
         * A function from the claimed proxy index (i.e. the index in the array of the value consumed
         * by the {@code resultConsumer} passed to {@link #attemptClaimOfProxyStates}) to the index in
         * {@code resultIndicesToClaimFor}.
         * If null, it will be assumed to be the identity (i.e. each index mapped to a singleton array of itself).
         */
        private final @Nullable Int2ObjectFunction<int[]> proxyToResultIndicesIndex;

        /**
         * @param resultIndicesToClaimFor   The value for {@link #resultIndicesToClaimFor}.
         * @param claimableStatesFunction   The value for {@link #claimableStatesFunction}.
         * @param proxyToResultIndicesIndex The value for {@link #proxyToResultIndicesIndex}.
         */
        protected AttemptToClaimStatesFillPromise(int @Nullable [] resultIndicesToClaimFor, Function<BlockState, SortedClaimableStates> claimableStatesFunction, @Nullable Int2ObjectFunction<int[]> proxyToResultIndicesIndex) {
            this.resultIndicesToClaimFor = resultIndicesToClaimFor;
            this.claimableStatesFunction = claimableStatesFunction;
            this.proxyToResultIndicesIndex = proxyToResultIndicesIndex;
        }

        @Override
        public void run() {
            // First, check if all targets are already filled
            if (this.claimIndex == 0) {
                boolean allFilled = true;
                if (this.resultIndicesToClaimFor != null) {
                    for (int resultIndexToClaimFor : this.resultIndicesToClaimFor) {
                        if (FilledArrayResultRequestProcessor.this.result.getResourcePackToState(resultIndexToClaimFor) == null) {
                            allFilled = false;
                            break;
                        }
                    }
                } else {
                    for (int resultIndexToClaimFor = 0; resultIndexToClaimFor < FilledArrayResultRequestProcessor.this.result.fromStates().length; resultIndexToClaimFor++) {
                        if (FilledArrayResultRequestProcessor.this.result.getResourcePackToState(resultIndexToClaimFor) == null) {
                            allFilled = false;
                            break;
                        }
                    }
                }
                if (allFilled) {
                    this.setDone();
                    return;
                }
                // Otherwise, perform the actual claim
                // Compute the claimable states
                int claimableStatesReferenceResultIndicesIndex = this.proxyToResultIndicesIndex != null ? this.proxyToResultIndicesIndex.get(0)[0] : 0;
                int claimableStatesReferenceFromStatesIndex = this.resultIndicesToClaimFor != null ? this.resultIndicesToClaimFor[claimableStatesReferenceResultIndicesIndex] : claimableStatesReferenceResultIndicesIndex;
                BlockState claimableStatesReference = FilledArrayResultRequestProcessor.this.result.fromStates()[claimableStatesReferenceFromStatesIndex];
                this.claimableStates = this.claimableStatesFunction.apply(claimableStatesReference);
                this.claimableStatesFunction = null;
                // Compute the priority
                BlockData[] blockStatesToClaimFor = this.resultIndicesToClaimFor != null ? Arrays.stream(this.resultIndicesToClaimFor).mapToObj(index -> FilledArrayResultRequestProcessor.this.result.fromStates()[index].asBlockData()).toArray(BlockData[]::new) : Arrays.stream(FilledArrayResultRequestProcessor.this.result.fromStates()).map(BlockState::asBlockData).toArray(BlockData[]::new);
                this.priority = ClaimRequestPriority.forBlockStates(blockStatesToClaimFor);
            }
            FilledArrayResultRequestProcessor.this.attemptClaimOfProxyStates(claimableStates.get(this.claimIndex), this.priority, proxyStates -> {
                if (proxyStates != null) {
                    for (int proxyStateIndex = 0; proxyStateIndex < proxyStates.length; proxyStateIndex++) {
                        int[] resultIndicesIndices = this.proxyToResultIndicesIndex != null ? this.proxyToResultIndicesIndex.get(proxyStateIndex) : new int[]{proxyStateIndex};
                        if (resultIndicesIndices.length > 0) {
                            BlockState proxyState = VanillaOnlyBlockStateRegistry.get().byId(proxyStates[proxyStateIndex]);
                            for (int resultIndicesIndex : resultIndicesIndices) {
                                int resultIndex = this.resultIndicesToClaimFor != null ? this.resultIndicesToClaimFor[resultIndicesIndex] : resultIndicesIndex;
                                FilledArrayResultRequestProcessor.this.result.setResourcePackToStateIfNotSet(resultIndex, proxyState, true);
                            }
                        }
                    }
                    this.setDone();
                } else {
                    if (++this.claimIndex == this.claimableStates.claims()) {
                        this.setDone();
                        return;
                    }
                    this.run();
                }
            });
        }

    }

    protected FillPromise attemptToClaimStatesFillPromiseForAllStatesAtOnce(Function<BlockState, SortedClaimableStates> claimableStatesFunction) {
        return new AttemptToClaimStatesFillPromise(null, claimableStatesFunction, null);
    }

    protected FillPromise attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlock(Function<BlockState, SortedClaimableStates> claimableStatesFunction, Block blockStatesReferenceBlock) {
        return attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlockStates(claimableStatesFunction, blockStatesReferenceBlock.getStateDefinition().getPossibleStatesArray(), ((FromToBlockStatesRequestBuilder) this.request).fromStates()[0].getProperties().equals(blockStatesReferenceBlock.getStateDefinition().getPossibleStates().getFirst().getProperties()));
    }

    protected FillPromise attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlockStates(Function<BlockState, SortedClaimableStates> claimableStatesFunction, BlockState[] referenceBlockStates) {
        return this.attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlockStates(claimableStatesFunction, referenceBlockStates, false);
    }

    protected FillPromise attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlockStates(Function<BlockState, SortedClaimableStates> claimableStatesFunction, BlockState[] referenceBlockStates, boolean proxyToResultIndicesIndexIsIdentity) {
        if (this.request instanceof FromToBlockStatesRequestBuilder fromStatesRequest) {
            @Nullable Int2ObjectFunction<int[]> proxyToResultIndicesIndex;
            if (proxyToResultIndicesIndexIsIdentity) {
                proxyToResultIndicesIndex = null;
            } else {
                @Nullable Int2ObjectMap<int[]>[] proxyToResultIndicesIndexMap = new Int2ObjectMap[]{null};
                proxyToResultIndicesIndex = proxyIndex -> {
                    if (proxyToResultIndicesIndexMap[0] == null) {
                        proxyToResultIndicesIndexMap[0] = new Int2ObjectOpenHashMap<>(referenceBlockStates.length);
                        int size = fromStatesRequest.fromStates().length;
                        IntSet resultIndicesLeft = new IntOpenHashSet(size);
                        for (int i = 0; i < size; i++) {
                            resultIndicesLeft.add(i);
                        }
                        List<Property<?>> referenceBlockStatesProperties = referenceBlockStates[0].getProperties().stream()
                            .filter(property -> {
                                Comparable<?> value = referenceBlockStates[0].getValue(property);
                                for (int i = 1; i < referenceBlockStates.length; i++) {
                                    if (!referenceBlockStates[i].getValue(property).equals(value)) {
                                        return true;
                                    }
                                }
                                return false;
                            }).toList();
                        for (int someProxyIndex = 0; someProxyIndex < referenceBlockStates.length; someProxyIndex++) {
                            BlockState referenceBlockState = referenceBlockStates[someProxyIndex];
                            IntList resultIndices = new IntArrayList(1);
                            IntIterator iterator = resultIndicesLeft.iterator();
                            while (iterator.hasNext()) {
                                int resultIndex = iterator.nextInt();
                                BlockState resultState = fromStatesRequest.fromStates()[resultIndex];
                                boolean same = true;
                                for (Property<?> property : referenceBlockStatesProperties) {
                                    if (resultState.hasProperty(property)) {
                                        if (!resultState.getValue(property).equals(referenceBlockState.getValue(property))) {
                                            same = false;
                                            break;
                                        }
                                    }
                                }
                                if (same) {
                                    resultIndices.add(resultIndex);
                                    iterator.remove();
                                }
                            }
                            proxyToResultIndicesIndexMap[0].put(someProxyIndex, resultIndices.toIntArray());
                        }
                    }
                    return proxyToResultIndicesIndexMap[0].get(proxyIndex);
                };
            }
            return new AttemptToClaimStatesFillPromise(null, claimableStatesFunction, proxyToResultIndicesIndex);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    protected FillPromise attemptToClaimStatesFillPromiseStateByState(Function<BlockState, SortedClaimableStates> claimableStatesFunction) {
        return this.attemptToClaimStatesFillPromiseStateByState(claimableStatesFunction, null);
    }

    protected FillPromise attemptToClaimStatesFillPromiseStateByState(Function<BlockState, SortedClaimableStates> claimableStatesFunction, @Nullable Int2ObjectFunction<int[]> proxyToResultIndicesIndex) {
        FilledArrayResultRequestProcessor<R, Re>.FillPromise[] promises = new FilledArrayResultRequestProcessor.FillPromise[FilledArrayResultRequestProcessor.this.result.fromStates().length];
        for (int i = 0; i < promises.length; i++) {
            promises[i] = new AttemptToClaimStatesFillPromise(new int[]{i}, claimableStatesFunction, proxyToResultIndicesIndex);
        }
        return new MultiplePromisesFillPromise(promises);
    }

    public interface FillPromiseGetter<R extends ProxyStatesRequestBuilderImpl, Re extends FilledArrayResultRequestProcessor.Result> {

        FilledArrayResultRequestProcessor<R, Re>.FillPromise get(FilledArrayResultRequestProcessor<R, Re> processor);

    }

    protected static <R extends ProxyStatesRequestBuilderImpl, Re extends FilledArrayResultRequestProcessor.Result> FillPromiseGetter<R, Re> claimStatesForAllStatesAtOnceForBlockStatesByDynamicProperties(List<Block> blocks, Property... properties) {
        BlockState[] referenceBlockStates = computeClaimableStatesForBaseWithDynamicProperties(blocks.getFirst().defaultBlockState(), properties);
        DynamicClaimableStates claimableStates = new ExplicitDynamicClaimableStates(() -> computeClaimableStatesForDynamicProperties(blocks.stream(), properties));
        return processor -> processor.attemptToClaimStatesFillPromiseForAllStatesAtOnceForBlockStates(claimableStates::get, referenceBlockStates);
    }

    public class BlockFallbackFillPromise extends FillPromise {

        protected final Block fallbackBlock;

        public BlockFallbackFillPromise(Block fallbackBlock) {
            this.fallbackBlock = fallbackBlock;
        }

        @Override
        public void run() {
            FilledArrayResultRequestProcessor.this.result.setAllUnmapped(this.fallbackBlock);
            this.setDone();
        }

    }

    public class StateFallbackFillPromise extends FillPromise {

        protected final BlockState fallbackState;

        public StateFallbackFillPromise(BlockState fallbackState) {
            this.fallbackState = fallbackState;
        }

        @Override
        public void run() {
            FilledArrayResultRequestProcessor.this.result.setAllUnmapped(this.fallbackState);
            this.setDone();
        }

    }

    public class StatesFallbackFillPromise extends FillPromise {

        protected final BlockState[] fallbackStates;

        public StatesFallbackFillPromise(BlockState[] fallbackStates) {
            this.fallbackStates = fallbackStates;
        }

        @Override
        public void run() {
            FilledArrayResultRequestProcessor.this.result.setAllUnmapped(this.fallbackStates);
            this.setDone();
        }

    }

    public static List<BlockState[]> computeClaimableStatesForDynamicProperties(Stream<Block> blocks, Property... dynamicProperties) {
        return blocks
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream().filter(state -> {
                for (Property<?> property : dynamicProperties) {
                    if (!state.getValue(property).equals(property.getPossibleValues().getFirst())) {
                        return false;
                    }
                }
                return true;
            }))
            .sorted(Comparator.comparing(state -> state == state.getBlock().defaultBlockState()))
            .map(base -> computeClaimableStatesForBaseWithDynamicProperties(base, dynamicProperties))
            .toList();
    }

    public static BlockState[] computeClaimableStatesForBaseWithDynamicProperties(BlockState base, Property... dynamicProperties) {
        int[] propertyValueIndices = new int[dynamicProperties.length];
        Arrays.fill(propertyValueIndices, -1);
        int size = 1;
        for (Property<?> property : dynamicProperties) {
            size *= property.getPossibleValues().size();
        }
        BlockState[] result = new BlockState[size];
        int resultIndex = 0;
        int propertyValueIndicesIndex = 0;
        BlockState state = base;
        while (resultIndex < size) {
            if (propertyValueIndicesIndex == propertyValueIndices.length) {
                result[resultIndex++] = state;
                propertyValueIndicesIndex--;
                continue;
            }
            int newPropertyValueIndex = ++propertyValueIndices[propertyValueIndicesIndex];
            Property<?> property = dynamicProperties[propertyValueIndicesIndex];
            if (newPropertyValueIndex == property.getPossibleValues().size()) {
                propertyValueIndices[propertyValueIndicesIndex] = -1;
                propertyValueIndicesIndex--;
                continue;
            }
            state = state.setValue((Property) property, (Comparable) property.getPossibleValues().get(newPropertyValueIndex));
            propertyValueIndicesIndex++;
        }
        return result;
    }

}
