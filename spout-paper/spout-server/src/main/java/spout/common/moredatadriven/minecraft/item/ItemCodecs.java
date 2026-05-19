package spout.common.moredatadriven.minecraft.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides base methods to create codecs for items.
 */
public final class ItemCodecs {

    private ItemCodecs() {
        throw new UnsupportedOperationException();
    }

    /**
     * Based on {@link BlockBehaviour#simpleCodec}.
     */
    public static <I extends Item> MapCodec<I> simpleCodec(
        Function<Item.Properties, I> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemPropertiesCodec.getBuilder()
        ).apply(instance, factory));
    }

    public static <I extends Item, T1> MapCodec<I> simpleCodec(
        App<RecordCodecBuilder.Mu<I>, T1> t1,
        BiFunction<T1, Item.Properties, I> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            ItemPropertiesCodec.getBuilder()
        ).apply(instance, factory));
    }

    public static <I extends Item, T1, T2> MapCodec<I> simpleCodec(
        App<RecordCodecBuilder.Mu<I>, T1> t1,
        App<RecordCodecBuilder.Mu<I>, T2> t2,
        Function3<T1, T2, Item.Properties, I> factory
    ) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
            t1,
            t2,
            ItemPropertiesCodec.getBuilder()
        ).apply(instance, factory));
    }

    public static <I extends BlockItem> MapCodec<I> blockCodec(
        BiFunction<Block, Item.Properties, I> factory
    ) {
        return simpleCodec(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlockItem::getBlock),
            (block, properties) -> factory.apply(block, properties.useBlockDescriptionPrefix())
        );
    }

}
