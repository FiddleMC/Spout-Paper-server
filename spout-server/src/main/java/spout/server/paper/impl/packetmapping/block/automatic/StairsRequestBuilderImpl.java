package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Blocks;
import spout.server.paper.api.packetmapping.block.automatic.StairsRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;

/**
 * The implementation of {@link StairsRequestBuilder}.
 */
public class StairsRequestBuilderImpl extends FromToBlockTypeRequestBuilderImpl<UsedStates.Stairs> implements StairsRequestBuilder {

    public StairsRequestBuilderImpl() {
        super();
        this.fallback = Blocks.STONE_STAIRS;
    }

}
