package spout.common.moredatadriven.minecraft.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BlockTypes;
import spout.client.fabric.moredatadriven.minecraft.type.WithItemCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Analogous to {@link BlockTypes}, but for {@link Item}s.
 */
public final class ItemTypes {

    private ItemTypes() {
        throw new UnsupportedOperationException();
    }

    public static final Map<Identifier, MapCodec<? extends Item>> MAP = new HashMap<>();

    static {
        Void registry = null;
        class Registry {
            static void register(Void registry, String name, MapCodec<? extends Item> value) {
                MAP.put(Identifier.parse(name), value);
            }
        }
        Registry.register(registry, "item", ItemCodecs.ITEM_CODEC);
        Registry.register(registry, "block", ItemCodecs.BLOCK_ITEM_CODEC);
        Registry.register(registry, "double_high_block", ItemCodecs.DOUBLE_HIGH_BLOCK_ITEM_CODEC);
        Registry.register(registry, "egg", ItemCodecs.EGG_ITEM_CODEC);
        // TODO others
    }

    public static final Codec<MapCodec<? extends Item>> BY_NAME_CODEC = Identifier.CODEC.comapFlatMap(
        identifier -> DataResult.success(MAP.get(identifier)),
        typeCodec -> null // Only needed on server
    );
    public static final MapCodec<Item> CODEC = BY_NAME_CODEC.dispatchMap(item -> ((WithItemCodec) item).codec(), Function.identity());

}
