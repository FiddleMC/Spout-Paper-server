package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Blocks;
import spout.server.paper.api.packetmapping.block.automatic.PressurePlateRequestBuilder;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;

/**
 * The implementation of {@link PressurePlateRequestBuilder}.
 */
public class PressurePlateRequestBuilderImpl extends FromToBlockTypeRequestBuilderImpl<UsedStates.PressurePlate> implements PressurePlateRequestBuilder {

    public PressurePlateRequestBuilderImpl() {
        super();
        this.fallback = Blocks.STONE_PRESSURE_PLATE;
    }

}
