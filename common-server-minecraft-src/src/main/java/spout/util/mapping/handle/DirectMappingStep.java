package spout.util.mapping.handle;

/**
 * A {@link MappingStep} that always maps to a specific value.
 */
public record DirectMappingStep<T, H extends AbstractMappingHandle<T>>(T to) implements MappingStep<H> {

    @Override
    public void apply(H handle) {
        handle.set(this.to);
    }

    @Override
    public boolean isDirect() {
        return true;
    }

}
