package spout.util.mapping.handle;

/**
 * A base implementation of {@link AbstractMappingHandle},
 * that also implements {@link AbstractMutableMappingHandle} and {@link AbstractWithOriginalMappingHandle}.
 */
public class SimpleMappingHandle<T, MT extends T> implements AbstractMutableMappingHandle<T, MT>, AbstractWithOriginalMappingHandle<T> {

    /**
     * The original data;
     */
    protected final T original;

    /**
     * The current data.
     */
    protected T data;

    /**
     * Whether the data is the original data.
     */
    protected boolean isDataOriginal = false;

    /**
     * Whether {@link #data} is mutable, and of type {@link MT}.
     */
    protected boolean isDataMutable;

    public SimpleMappingHandle(T data, boolean isDataMutable) {
        this.original = data;
        this.data = data;
        this.isDataMutable = isDataMutable;
    }

    @Override
    public T getImmutable() {
        return this.data;
    }

    private void set(T data, boolean isDataMutableAfterwards) {
        this.data = data;
        this.isDataOriginal = false;
        this.isDataMutable = isDataMutableAfterwards;
    }

    @Override
    public void set(T data) {
        this.set(data, false);
    }

    @Override
    public MT getMutable() {
        if (!this.isDataMutable) {
            this.setMutable(this.cloneMutable(this.data));
        }
        return (MT) this.data;
    }

    @Override
    public void setMutable(MT data) {
        this.set(data, true);
    }

    /**
     * @param data A {@link T} instance.
     * @return A new {@link MT} instance with the same contents as the given instance.
     */
    protected MT cloneMutable(T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getOriginal() {
        return this.original;
    }

}
