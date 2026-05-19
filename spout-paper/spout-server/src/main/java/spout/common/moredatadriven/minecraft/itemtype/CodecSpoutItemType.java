package spout.common.moredatadriven.minecraft.itemtype;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import spout.common.moredatadriven.minecraft.item.SpoutDataDrivenItem;
import spout.common.util.mojang.codec.MapInputAndOps;
import java.util.stream.Stream;

/**
 * An implementation of {@link SpoutItemType} defined by its codec.
 */
public final class CodecSpoutItemType implements SpoutItemType {

    private final Identifier identifier;
    private final MapCodec<? extends Item> minecraftCodec;
    private final MapCodec<SpoutDataDrivenItem> codec;

    public CodecSpoutItemType(Identifier identifier, MapCodec<? extends Item> minecraftCodec) {
        this.identifier = identifier;
        this.minecraftCodec = minecraftCodec;
        this.codec = new MapCodec<>() {

            @Override
            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                return CodecSpoutItemType.this.minecraftCodec.keys(dynamicOps);
            }

            @Override
            public <T> RecordBuilder<T> encode(SpoutDataDrivenItem input, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
                return ((MapCodec<Item>) CodecSpoutItemType.this.minecraftCodec).encode(input.getItem(), dynamicOps, recordBuilder);
            }

            @Override
            public <T> DataResult<SpoutDataDrivenItem> decode(DynamicOps<T> dynamicOps, MapLike<T> input) {
                return DataResult.success(new SpoutDataDrivenItem(CodecSpoutItemType.this, new MapInputAndOps<>(input, dynamicOps)));
            }

        };
    }

    @Override
    public Identifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public MapCodec<SpoutDataDrivenItem> getCodec() {
        return this.codec;
    }

    @Override
    public <T> DataResult<? extends Item> decodeItemFromInput(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
        return this.minecraftCodec.decode(dynamicOps, mapLike);
    }

}
