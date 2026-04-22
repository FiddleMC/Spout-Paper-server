package spout.server.paper.impl.packetmapping.block;

import java.util.function.Consumer;
import spout.server.paper.api.packetmapping.block.BlockMappingHandle;

/**
 * A {@link BlockMappingsStep} that performs a specific function.
 */
public record BukkitFunctionBlockMappingsStep(Consumer<BlockMappingHandle> function, boolean requiresCoordinates) implements FunctionBlockMappingsStep {

    @Override
    public void apply(BlockMappingHandleNMSImpl handle) {
        this.function.accept(handle.bukkitHandle());
    }

}
