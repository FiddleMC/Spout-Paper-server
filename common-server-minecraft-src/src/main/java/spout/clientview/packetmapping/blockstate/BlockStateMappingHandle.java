package spout.clientview.packetmapping.blockstate;

import net.minecraft.world.level.block.state.BlockState;
import spout.util.mapping.handle.AbstractMappingHandle;
import spout.util.mapping.handle.SimpleWithContextMappingHandle;

/**
 * The implementation of {@link AbstractMappingHandle} for block state mappings.
 */
public class BlockStateMappingHandle extends SimpleWithContextMappingHandle<BlockState, BlockState, BlockStateMappingsApplicationContext> {

    public BlockStateMappingHandle(BlockState data, BlockStateMappingsApplicationContext context, boolean isDataMutable) {
        super(data, context, isDataMutable);
    }

}
