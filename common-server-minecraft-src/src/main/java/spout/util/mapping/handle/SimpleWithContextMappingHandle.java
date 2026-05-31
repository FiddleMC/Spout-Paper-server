package spout.util.mapping.handle;

/**
 * A base implementation of {@link AbstractWithContextMappingHandle},
 * that builds upon {@link SimpleMappingHandle}.
 */
public class SimpleWithContextMappingHandle<T, MT extends T, C> extends SimpleMappingHandle<T, MT> implements AbstractWithContextMappingHandle<T, C> {

    /**
     * The context.
     */
    protected final C context;

    public SimpleWithContextMappingHandle(T data, C context, boolean isDataMutable) {
        super(data, isDataMutable);
        this.context = context;
    }

    @Override
    public C getContext() {
        return this.context;
    }

}
