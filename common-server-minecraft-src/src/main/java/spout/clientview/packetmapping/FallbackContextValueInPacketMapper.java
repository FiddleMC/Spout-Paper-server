package spout.clientview.packetmapping;

import org.jspecify.annotations.Nullable;

/**
 * A {@link ValueInPacketMapper} that has a generic fallback context.
 */
public interface FallbackContextValueInPacketMapper<I, O, C> extends ValueInPacketMapper<I, O, C> {

    C getFallbackContext();

    O applyWithNonNullContext(I value, C context);

    @Override
    default O apply(I value, @Nullable C context) {
        return this.applyWithNonNullContext(value, context != null ? context : this.getFallbackContext());
    }

    @Override
    default O apply(I value) {
        return this.applyWithNonNullContext(value, this.getFallbackContext());
    }

}
