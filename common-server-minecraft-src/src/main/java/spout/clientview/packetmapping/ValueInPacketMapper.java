package spout.clientview.packetmapping;

import org.jspecify.annotations.Nullable;

/**
 * An abstract mapper of values in packets.
 *
 * @param <I> The input to the mapper.
 * @param <O> The output from the  mapper.
 * @param <C> The type of the context of in which the mapper is applied.
 */
public interface ValueInPacketMapper<I, O, C> {

    /**
     * Applies all relevant mappings to the given value,
     * within the given context.
     *
     * @param context The context, or null if none (in other words, the context is generic).
     */
    O apply(I value, @Nullable C context);

    /**
     * Applies all relevant mappings to the given value,
     * with a generic context.
     */
    default O apply(I value) {
        return this.apply(value, null);
    }

}
