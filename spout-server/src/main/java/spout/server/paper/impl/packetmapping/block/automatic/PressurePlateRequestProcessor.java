package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;

/**
 * A {@link RequestProcessor} for {@link PressurePlateRequestBuilderImpl}s.
 */
public class PressurePlateRequestProcessor extends MatchingBlockStateClaimAttemptsRequestProcessor<UsedStates.PressurePlate, PressurePlateRequestBuilderImpl> {

    public PressurePlateRequestProcessor(PressurePlateRequestBuilderImpl request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    @Override
    protected Block[] createBlocksToClaim() {
        return new Block[0];
    }

    @Override
    protected UsedStates.PressurePlate createUsedStates(BlockState @Nullable [] result) {
        return result != null ? new UsedStatesImpl.PressurePlateImpl(result[0].getBlock(), false) : new UsedStatesImpl.PressurePlateImpl(this.request.fallback, true);
    }

}
