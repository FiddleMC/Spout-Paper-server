package spout.common.moredatadriven.minecraft.blocktype;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import spout.common.moredatadriven.minecraft.BuiltInSpoutMoreDataDrivenRegistries;
import spout.common.moredatadriven.minecraft.SpoutMoreDataDrivenRegistries;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * The class for {@link BuiltInSpoutMoreDataDrivenRegistries#BLOCK_TYPE}.
 */
public final class SpoutBlockTypeRegistry extends MappedRegistry<SpoutBlockType> {

    private final Map<MapCodec<? extends Block>, SpoutBlockType> byCodec = new IdentityHashMap<>();

    public SpoutBlockTypeRegistry() {
        super(SpoutMoreDataDrivenRegistries.BLOCK_TYPE, Lifecycle.stable());
    }

    @Override
    public Holder.Reference<SpoutBlockType> register(ResourceKey<SpoutBlockType> key, SpoutBlockType value, RegistrationInfo registrationInfo) {
        @Nullable MapCodec<? extends Block> blockClassCodec = value.getBlockClassCodec();
        if (blockClassCodec != null) {
            this.byCodec.put(blockClassCodec, value);
        }
        return super.register(key, value, registrationInfo);
    }

    /**
     * @return The {@link SpoutBlockType} with the given {@link Block#codec}.
     */
    public @Nullable SpoutBlockType byBlockCodec(MapCodec<? extends Block> codec) {
        return this.byCodec.get(codec);
    }

}
