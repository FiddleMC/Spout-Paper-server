package spout.common.moredatadriven.minecraft.block;

import net.minecraft.world.level.block.Block;
import spout.common.moredatadriven.minecraft.BuiltInSpoutMoreDataDrivenRegistries;
import spout.common.moredatadriven.minecraft.blocktype.SpoutBlockType;
import java.util.Objects;

/**
 * An interface to get the type of some {@link Block}.
 *
 * <p>
 * This is specifically meant to be implemented by {@link Block}.
 * </p>
 */
public interface BlockWithType {

    default SpoutBlockType spout$getBlockType() {
        // return Objects.requireNonNull(BuiltInSpoutMoreDataDrivenRegistries.BLOCK_TYPE.byBlockCodec(((BlockCodecAccessor) this).spout$codec()));
        return null; // TODO remove temp
    }

}
