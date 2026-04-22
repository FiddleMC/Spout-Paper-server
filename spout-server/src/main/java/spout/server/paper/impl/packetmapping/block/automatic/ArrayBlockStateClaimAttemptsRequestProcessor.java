package spout.server.paper.impl.packetmapping.block.automatic;

import net.minecraft.world.level.block.state.BlockState;
import spout.server.paper.api.packetmapping.block.automatic.UsedStates;
import spout.server.paper.impl.packetmapping.block.BlockMappingsComposeEventImpl;
import org.jspecify.annotations.Nullable;

/**
 * A {@link BlockStateClaimAttemptsRequestProcessor}
 * where the states to claim are stored in an array in advance.
 */
public abstract class ArrayBlockStateClaimAttemptsRequestProcessor<US extends UsedStates, R extends ProxyStatesRequestBuilderImpl<US> & FromToBlockStatesRequestBuilder> extends BlockStateClaimAttemptsRequestProcessor<US, R> {

    private BlockState @Nullable [][] statesToClaim;
    private int index;

    protected ArrayBlockStateClaimAttemptsRequestProcessor(R request, BlockMappingsComposeEventImpl event) {
        super(request, event);
    }

    protected abstract BlockState[][] createStatesToClaim();

    private BlockState[][] getStatesToClaim() {
        if (this.statesToClaim == null) {
            this.statesToClaim = this.createStatesToClaim();
        }
        return this.statesToClaim;
    }

    @Override
    protected BlockState[] nextCandidates() {
        return this.getStatesToClaim()[this.index++];
    }

    @Override
    protected boolean hasMoreAttempts() {
        return this.index < this.getStatesToClaim().length;
    }

}
