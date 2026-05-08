package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Common interface for {@link FromToBlockStateRequestBuilderImpl}
 * and {@link FromToBlockTypeRequestBuilderImpl}.
 */
public interface FromToBlockStatesRequestBuilder {

    BlockState[] fromStates();

    BlockState[] fallbackStates();

}
