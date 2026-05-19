package spout.common.util.mojang.codec;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import java.util.function.BiFunction;

/**
 * Similar to {@link InputAndOps}, but for a {@link MapLike} input for a {@link MapCodec}.
 */
public record MapInputAndOps<T>(MapLike<T> input, DynamicOps<T> ops) {

    public <A> DataResult<A> decode(BiFunction<DynamicOps<?>, MapLike<?>, DataResult<A>> decoder) {
        return decoder.apply(this.ops, this.input);
    }

    public <A> A decodeValue(BiFunction<DynamicOps<?>, MapLike<?>, DataResult<A>> decoder) {
        return this.decode(decoder).getOrThrow();
    }

}
