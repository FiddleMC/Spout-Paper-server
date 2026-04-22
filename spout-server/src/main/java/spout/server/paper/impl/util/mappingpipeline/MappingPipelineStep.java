package spout.server.paper.impl.util.mappingpipeline;

import spout.server.paper.api.util.mapping.MappingFunctionHandle;

/**
 * A step that is applied as part of a mapping pipeline,
 * defined as a single operation.
 */
public interface MappingPipelineStep<H extends MappingFunctionHandle<?>> {

    /**
     * Applies this mapping.
     *
     * @param handle The handle being mapped.
     */
    void apply(H handle);

}
