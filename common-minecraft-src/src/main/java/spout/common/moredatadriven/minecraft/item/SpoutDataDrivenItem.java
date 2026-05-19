package spout.common.moredatadriven.minecraft.item;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;
import spout.common.moredatadriven.minecraft.common.dependent.DependentDataDrivenResource;
import spout.common.moredatadriven.minecraft.BuiltInSpoutMoreDataDrivenRegistries;
import spout.common.moredatadriven.minecraft.itemtype.SpoutItemType;
import spout.common.util.mojang.codec.MapInputAndOps;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * A data-driven version of {@link Item}.
 *
 * <p>
 * It wraps an {@link Item}.
 * For received data-driven items, this is initially null, until initialized.
 * For built-in items, this is non-null.
 * </p>
 */
public class SpoutDataDrivenItem implements DependentDataDrivenResource {

    /**
     * A codec for {@link SpoutDataDrivenItem}s.
     */
    public static final MapCodec<SpoutDataDrivenItem> CODEC = BuiltInSpoutMoreDataDrivenRegistries.ITEM_TYPE.byNameCodec().dispatchMap(item -> item.type, SpoutItemType::getCodec);

    /**
     * An encoder for {@link Item}s using {@link #CODEC}.
     */
    public static final Encoder<Item> MINECRAFT_ENCODER = new Encoder<>() {

        @Override
        public <T> DataResult<T> encode(Item input, DynamicOps<T> dynamicOps, T prefix) {
            return CODEC.encoder().encodeStart(dynamicOps, new SpoutDataDrivenItem(((ItemWithType) input).spout$getItemType(), input));
        }

    };

    /**
     * The data-driven type of this instance.
     */
    public final SpoutItemType type;

    /**
     * The {@link Item} instance that this instance represents,
     * or null if not initialized yet.
     */
    private @Nullable Item item;

    /**
     * The received information to later construct the {@link #item} value,
     * or null if not necessary.
     */
    private @Nullable MapInputAndOps<?> input;

    public SpoutDataDrivenItem(SpoutItemType type, MapInputAndOps<?> input) {
        this.type = type;
        this.input = input;
    }

    public SpoutDataDrivenItem(SpoutItemType type, Item item) {
        this.type = type;
        this.item = item;
    }

    /**
     * @return The {@link Item} instance that this instance represents.
     * @throws NullPointerException If the {@link Item} instance is not initialized yet.
     */
    public Item getItem() {
        return Objects.requireNonNull(this.item);
    }

    public void initializeItemFromInput() {
        this.item = Objects.requireNonNull(this.input).<Item>decodeValue(((dynamicOps, mapLike) -> SpoutDataDrivenItem.this.type.decodeItemFromInput((DynamicOps) dynamicOps, (MapLike) mapLike)));
        this.input = null;
    }

    @Override
    public Collection<Identifier> getRequiredResources() {
        return Collections.emptyList();
    }

}
