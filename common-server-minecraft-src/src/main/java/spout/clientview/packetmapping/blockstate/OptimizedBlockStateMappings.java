package spout.clientview.packetmapping.blockstate;

import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.clientview.model.ClientView;
import spout.util.mapping.handle.MappingStep;

/**
 * Holds all block state mappings
 * in an optimized data structure.
 */
public final class OptimizedBlockStateMappings {

    private OptimizedBlockStateMappings() {
        throw new UnsupportedOperationException();
    }

    /**
     * The registered mappings that must be performed in chains:
     * this includes only the mappings for {@link BlockState}s for which there was
     * at least 1 non-{@linkplain MappingStep#isDirect() direct} step.
     *
     * <p>
     * The mappings are organized in an array where {@link ClientView.AwarenessLevel#getId()}
     * is the index, and then in an array where {@link BlockState#indexInBlockStateRegistry} is the index.
     * The lowest-level array may be null, but will never be empty.
     * </p>
     */
    private static final MappingStep<BlockStateMappingHandle>[][][] chains = new MappingStep[ClientView.AwarenessLevel.getAll().length][][];

    /**
     * The registered mappings that can be applied directly:
     * this includes only the mappings for {@link BlockState}s for which there were only
     * {@linkplain MappingStep#isDirect() direct} steps,
     * meaning the mapping always returns the same value.
     *
     * <p>
     * The mappings are organized in an array where {@link ClientView.AwarenessLevel#getId()}
     * is the index, and then in an array where {@link BlockState#indexInBlockStateRegistry} is the index.
     * The lowest-level value may be null.
     * </p>
     */
    private static final @Nullable BlockState[][] direct = new BlockState[chains.length][];

    /**
     * The same as {@link #direct}, but contains the {@link BlockState#indexInBlockStateRegistry},
     * or -1 if the value in {@link #direct} would be null.
     */
    private static final int[][] directAsIndicesInRegistry = new int[chains.length][];

    public static @Nullable BlockState getDirect(int awarenessLevelId, int stateIndexInRegistry) {
        return direct[awarenessLevelId][stateIndexInRegistry];
    }

    public static MappingStep<BlockStateMappingHandle> @Nullable [] getChain(int awarenessLevelId, int stateIndexInRegistry) {
        return chains[awarenessLevelId][stateIndexInRegistry];
    }

}
