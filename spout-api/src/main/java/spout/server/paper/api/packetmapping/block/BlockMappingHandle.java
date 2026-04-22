package spout.server.paper.api.packetmapping.block;

import org.bukkit.block.data.BlockData;
import spout.server.paper.api.util.mapping.WithContextMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link BlockMappingBuilder#to}.
 */
public interface BlockMappingHandle extends WithContextMappingFunctionHandle<BlockData, BlockMappingFunctionContext>, WithOriginalMappingFunctionHandle<BlockData> {
}
