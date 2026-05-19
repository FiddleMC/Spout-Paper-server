package spout.common.moredatadriven.minecraft.blocktype;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

/**
 * A block type, which represents an implementation of a {@link Block}.
 */
public interface SpoutBlockType {

    Identifier getIdentifier();

    @Nullable MapCodec<? extends Block> getBlockClassCodec();

}
