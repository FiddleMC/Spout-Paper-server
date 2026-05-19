package spout.common.moredatadriven.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

/**
 * An accessor interface for {@link Block#codec()}.
 */
public interface BlockCodecAccessor {

    MapCodec<? extends Block> spout$codec();

}
