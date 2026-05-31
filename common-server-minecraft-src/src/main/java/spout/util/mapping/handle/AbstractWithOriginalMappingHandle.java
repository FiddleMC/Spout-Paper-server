package spout.util.mapping.handle;

/**
 * An {@link AbstractMappingHandle} where the original data can be observed.
 */
public interface AbstractWithOriginalMappingHandle<T> extends AbstractMappingHandle<T> {

    /**
     * @return The original data, before any mappings were applied.
     * The returned instance must not be modified.
     */
    T getOriginal();

}
