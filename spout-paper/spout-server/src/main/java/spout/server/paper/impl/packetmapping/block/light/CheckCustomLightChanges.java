package spout.server.paper.impl.packetmapping.block.light;

import net.minecraft.world.level.block.state.BlockState;
import spout.clientview.model.ClientView;
import spout.clientview.model.ClientViewImpl;
import spout.server.paper.impl.packetmapping.block.BlockMappingFunctionContextImpl;
import java.util.Arrays;

/**
 * A utility class to check whether light changes require extra sending
 * to client who will not simulate them the same.
 */
public final class CheckCustomLightChanges {

    private CheckCustomLightChanges() {
        throw new UnsupportedOperationException();
    }

    private static final BlockMappingFunctionContextImpl[] simulatedContexts = Arrays.stream(ClientView.AwarenessLevel.getThatDoNotAlwaysUnderstandsAllServerSideBlocks()).map(awarenessLevel ->
        new BlockMappingFunctionContextImpl(ClientViewImpl.getSimulatedForAwarenessLevel(awarenessLevel))
    ).toArray(BlockMappingFunctionContextImpl[]::new);

    public static boolean requiresExtraLightPacketSending(BlockState oldState, BlockState newState) {
        for (BlockMappingFunctionContextImpl simulatedContext : simulatedContexts) {
            if (spout.server.paper.impl.packetmapping.block.BlockMappingsImpl.get().apply(oldState, simulatedContext).getLightEmission() != oldState.getLightEmission() || spout.server.paper.impl.packetmapping.block.BlockMappingsImpl.get().apply(newState, simulatedContext).getLightEmission() != newState.getLightEmission()) {
                return true;
            }
        }
        return false;
    }

}
