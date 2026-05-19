package spout.common.moredatadriven.minecraft.blocktype;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

/**
 * An implementation of {@link SpoutBlockType} defined by its codec.
 */
public final class CodecSpoutBlockType implements SpoutBlockType {

    public final Identifier identifier;
    public final MapCodec<? extends Block> minecraftCodec;

    public CodecSpoutBlockType(Identifier identifier, MapCodec<? extends Block> minecraftCodec) {
        this.identifier = identifier;
        this.minecraftCodec = minecraftCodec;
    }

    @Override
    public Identifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public @Nullable MapCodec<? extends Block> getBlockClassCodec() {
        return this.minecraftCodec;
    }

}
