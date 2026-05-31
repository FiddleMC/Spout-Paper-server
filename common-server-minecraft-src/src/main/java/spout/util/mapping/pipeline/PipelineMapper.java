package spout.util.mapping.pipeline;

import spout.util.mapping.handle.AbstractMappingHandle;
import spout.util.mapping.handle.MappingStep;

/**
 * A utility class to apply mappings as a pipeline of individual steps.
 */
public final class PipelineMapper {

    private PipelineMapper() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@linkplain MappingStep#apply Applies} all the given steps to the input.
     *
     * @param handle The handle to map. The data in it may be mutated.
     * @param steps The steps to apply.
     * @return The resulting data, which may be the given instance if no changes were made.
     */
    public static <T, H extends AbstractMappingHandle<T>> T apply(H handle, Iterable<MappingStep<H>> steps) {
        for (MappingStep<H> mapping : steps) {
            mapping.apply(handle);
        }
        return handle.getImmutable();
    }

    /**
     * @see #apply(AbstractMappingHandle, Iterable) 
     */
    public static <T, H extends AbstractMappingHandle<T>> T apply(H handle, MappingStep<H>[] steps) {
        for (MappingStep<H> mapping : steps) {
            mapping.apply(handle);
        }
        return handle.getImmutable();
    }

}
