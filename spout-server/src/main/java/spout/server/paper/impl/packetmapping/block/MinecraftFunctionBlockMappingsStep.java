package spout.server.paper.impl.packetmapping.block;

import spout.server.paper.api.packetmapping.block.nms.BlockMappingHandleNMS;
import java.util.function.Consumer;

/**
 * A {@link BlockMappingsStep} that performs a specific function.
 */
public record MinecraftFunctionBlockMappingsStep(Consumer<BlockMappingHandleNMS> function, boolean requiresCoordinates) implements FunctionBlockMappingsStep {

    @Override
    public void apply(BlockMappingHandleNMSImpl handle) {
        this.function.accept(handle);
    }

}
