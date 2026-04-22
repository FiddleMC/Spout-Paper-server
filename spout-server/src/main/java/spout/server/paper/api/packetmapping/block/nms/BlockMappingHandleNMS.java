package spout.server.paper.api.packetmapping.block.nms;

import net.minecraft.world.level.block.state.BlockState;
import spout.server.paper.api.packetmapping.block.BlockMappingFunctionContext;
import spout.server.paper.api.util.mapping.WithContextMappingFunctionHandle;
import spout.server.paper.api.util.mapping.WithOriginalMappingFunctionHandle;

/**
 * A handle provided to code registered with {@link BlockMappingBuilderNMS#function}.
 */
public interface BlockMappingHandleNMS extends WithContextMappingFunctionHandle<BlockState, BlockMappingFunctionContext>, WithOriginalMappingFunctionHandle<BlockState> {
}
