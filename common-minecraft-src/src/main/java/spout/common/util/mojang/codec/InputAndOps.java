package spout.common.util.mojang.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;

/**
 * A pair of input and the corresponding {@link DynamicOps} to decode it.
 */
public record InputAndOps<T>(T input, DynamicOps<T> ops) {

    public <A> DataResult<Pair<A, T>> decode(Decoder<A> decoder) {
        return decoder.decode(this.ops, this.input);
    }

    public <A> A decodeValue(Decoder<A> decoder) {
        return this.decode(decoder).getOrThrow().getFirst();
    }

}
