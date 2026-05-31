package spout.util.mapping.handle;

/**
 * An {@link AbstractMappingHandle} that can provide context for the current mapping.
 */
public interface AbstractWithContextMappingHandle<T, C> extends AbstractMappingHandle<T> {

    /**
     * @return The context for which the current mapping is being applied.
     */
    C getContext();

}
