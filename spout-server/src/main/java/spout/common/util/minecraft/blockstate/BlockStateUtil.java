package spout.common.util.minecraft.blockstate;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * A utility for common {@link BlockState} operations.
 */
public final class BlockStateUtil {

    private BlockStateUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return The given {@code target}, but with every {@link Property} shared between
     * {@code source} and {@code target} set to the corresponding value of {@code source}.
     */
    public static BlockState copyProperties(BlockState source, BlockState target) {
        BlockState result = target;
        for (Property<?> property : source.getProperties()) {
            if (target.hasProperty(property)) {
                result = result.setValue((Property) property, (Comparable) source.getValue(property));
            }
        }
        return result;
    }

    /**
     * The same as {@link #copyProperties(BlockState, BlockState)},
     * but on the {@link Block#defaultBlockState()} of the given {@code target}.
     */
    public static BlockState copyProperties(BlockState source, Block target) {
        return copyProperties(source, target.defaultBlockState());
    }

    /**
     * The same as {@link #copyProperties(BlockState, BlockState)},
     * but for each pair (by index) in the arrays.
     */
    public static BlockState[] copyProperties(BlockState[] source, BlockState[] target) {
        BlockState[] result = new BlockState[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = copyProperties(source[i], target[i]);
        }
        return result;
    }

    /**
     * The same as {@link #copyProperties(BlockState[], BlockState[])},
     * but on the {@link Block#defaultBlockState()}s of the given {@code target}.
     */
    public static BlockState[] copyProperties(BlockState[] source, Block[] target) {
        BlockState[] result = new BlockState[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = copyProperties(source[i], target[i].defaultBlockState());
        }
        return result;
    }

    /**
     * The same as {@link #copyProperties(BlockState[], BlockState[])},
     * but on the {@link Block#defaultBlockState()} of the given {@code target}.
     */
    public static BlockState[] copyProperties(BlockState[] source, Block target) {
        BlockState[] result = new BlockState[source.length];
        BlockState targetState = target.defaultBlockState();
        for (int i = 0; i < source.length; i++) {
            result[i] = copyProperties(source[i], targetState);
        }
        return result;
    }

}
