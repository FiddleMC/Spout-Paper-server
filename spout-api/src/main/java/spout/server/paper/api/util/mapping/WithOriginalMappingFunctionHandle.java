package spout.server.paper.api.util.mapping;

/**
 * A {@link MappingFunctionHandle} where the original data can be observed.
 */
public interface WithOriginalMappingFunctionHandle<T> extends MappingFunctionHandle<T> {

    /**
     * @return The original data, before any mappings were applied.
     * The returned instance must not be modified.
     */
    T getOriginal();

}
