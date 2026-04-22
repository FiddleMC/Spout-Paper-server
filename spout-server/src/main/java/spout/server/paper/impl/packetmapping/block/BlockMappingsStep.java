package spout.server.paper.impl.packetmapping.block;

import spout.server.paper.impl.util.mappingpipeline.MappingPipelineStep;

/**
 * A mapping that is stored in {@link BlockMappingsImpl}.
 */
public sealed interface BlockMappingsStep extends MappingPipelineStep<BlockMappingHandleNMSImpl> permits SimpleBlockMappingsStep, FunctionBlockMappingsStep {
}
