package spout.server.paper.api.moredatadriven.paper.registry.type.nms;

import net.minecraft.world.level.block.Block;
import spout.server.paper.api.moredatadriven.paper.registry.type.BlockTypeType;

/**
 * Extension of {@link BlockTypeType} using Minecraft internals.
 */
public interface BlockTypeTypeNMS<B extends Block> extends BlockTypeType {

    @Override
    WrappedBlockCodec<B> getWrappedCodec();

}
